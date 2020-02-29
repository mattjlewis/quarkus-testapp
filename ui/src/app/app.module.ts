import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';

import { DepartmentsComponent } from './departments/departments.component';
import { DepartmentDetailComponent } from './department-detail/department-detail.component';
import { MessagesComponent } from './messages/messages.component';
import { DepartmentSearchComponent } from './department-search/department-search.component';

@NgModule({
  declarations: [
    AppComponent,
    DepartmentsComponent,
    DepartmentDetailComponent,
    MessagesComponent,
    DepartmentSearchComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
