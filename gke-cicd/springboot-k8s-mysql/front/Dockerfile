#FROM node:14 as builder
#WORKDIR /app
#COPY package.json .
#RUN npm i
#COPY . .
#EXPOSE 4200
#CMD["npm","start"]

FROM nginx
COPY dist/front /usr/share/nginx/html
