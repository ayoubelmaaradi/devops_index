def mavenBuild() {
    sh 'mvn clean package'
}

def buildFrontEnd() {
    sh 'npm build --prod'
}

def tagDockerImage(String dockerRegistry, String imageName,String commitId, String version) {
    return "${dockerRegistry}/${imageName}:${commitId}-${version}"
}

def buildAndPushDockerImage(String tag) {
    sh 'docker build -t '${tag}' .'
    sh 'docker login -u  -p  '
    sh 'docker push '${tag}
}
// ----****************************************************----------


   // --------------------------------------------------------------------------------------------------------------------------


    def getShortCommitId() {
    return sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
}

def getGitCurrentVersionTag() {
    def tag = sh(script: 'git describe --tags --exact-match || echo "noTag"', returnStdout: true).trim()
    if (tag ==~ /v[\d\.]+/) {
        return tag
    }
    return null
}

/**
* Return the version that should be used to tag the application.
* It is based on the version in the pom.xml, the current branch and its tag, so:
*   - for release or hotfix branch with a git tag like 'v.x.x.x' => return the pom version
*   - for release or hotfix branch without a git tag like 'v.x.x.x' => return <pom version>-rc.<build number>
*   - otherwise => return <pom version>-<commitId>
*/
def defineAppVersion(pomVersion, commitId, branchName, gitVersionTag) {
    def appVersion = "${pomVersion}-${commitId}"
    if (branchName.startsWith('release/') || branchName.startsWith('hotfix/')) {
        if(gitVersionTag != null && !gitVersionTag.isEmpty()) {
            appVersion = pomVersion
        } else {
            appVersion = "${pomVersion}-rc.${env.BUILD_NUMBER}"
        }
    }
    return appVersion
}

def sonarAnalysis(pom, branchName) {
    withMaven(mavenSettingsConfig: 'AF_MavenSettings') {
        withSonarQubeEnv('Sonar Agile Fabric') {
            def mvnSonarArgs = ''
            if (branchName == 'develop'){
                mvnSonarArgs = " -Dsonar.projectVersion=\'${pom.version}\'"
            } else {
                def sonarProps = readProperties(file: './sonar-project.properties')
                mvnSonarArgs = "-Dsonar.projectVersion=\'${branchName}-${pom.version}\'" +
                    " -Dsonar.projectKey=\'${sonarProps['sonar.projectKey']}-SNAPSHOT\'" +
                    " -Dsonar.projectName=\'${sonarProps['sonar.projectName']} - SNAPSHOT\'"
            }
            sh "./mvnw initialize sonar:sonar ${mvnSonarArgs}"
        }
    }
}

def packageChart(nexusRepos, dockerRegistry, appVersion) {
    docker.withRegistry("https://${dockerRegistry}", 'portus-credentials') {
        docker.image('docker-registry.agilefabric.fr.carrefour.com/ingelog/tools/helm-kubectl:v3.2.0').inside() {
            sh "mkdir -p ${WORKSPACE}/packages-charts"
            sh "sed -i -e 's/version:.*/version: ${appVersion}/' chart/supplatine-app-admin/Chart.yaml"
            sh "sed -i -e 's/appVersion:.*/appVersion: ${appVersion}/' chart/supplatine-app-admin/Chart.yaml"
            sh "helm package chart/supplatine-app-admin/ --destination ${WORKSPACE}/packages-charts"
            sh "helm repo index packages-charts --url ${nexusRepos}  --merge ${WORKSPACE}/packages-charts/index.yaml"
            withCredentials([usernamePassword(credentialsId: 'nexus-cred-helm', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')])
            {
                sh "curl -k -u ${NEXUS_USERNAME}:${NEXUS_PASSWORD} --upload-file ${WORKSPACE}/packages-charts/supplatine-app-admin-${appVersion}.tgz  ${nexusRepos}/supplatine-app-admin-${appVersion}.tgz"
            }
        }
    }
}

def buildImage(dockerConfig, commitId) {
    sh "cp -R src/main/docker/* target/"
    return docker.build("${dockerConfig.registry}/${dockerConfig.imageName}:${commitId}", 'target/')
}

def pushImageAndTag(image, dockerRegistry, tag) {
    docker.withRegistry("https://${dockerRegistry}", 'portus-credentials') {
        image.push()
        image.push(tag)
    }
}

def launchDeployment(deployJob, appVersion, env) {
    build job: deployJob, quietPeriod: 1, wait: false, parameters: [
        [$class: 'StringParameterValue', name: 'APP_NAME', value: 'supplatine-app-admin'],
        [$class: 'StringParameterValue', name: 'APP_VERSION', value: appVersion],
        [$class: 'StringParameterValue', name: 'APP_ENV', value: env]
    ]
}