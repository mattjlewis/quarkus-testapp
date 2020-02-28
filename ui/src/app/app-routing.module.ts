import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DepartmentsComponent } from './departments/departments.component';
import { DepartmentDetailComponent } from './department-detail/department-detail.component';

const routes: Routes = [
  { path: '', redirectTo: '/departments', pathMatch: 'full' },
  { path: 'departments', component: DepartmentsComponent },
  { path: 'department/:id', component: DepartmentDetailComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
