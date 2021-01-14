import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment.prod";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  list = [];
  constructor(private api: HttpClient) {
  }
  title = 'front';
  public callApi() {
    this.api.get(environment.apiUrl+'/users').subscribe(res => {
      console.log(res);
    });
  }
}
