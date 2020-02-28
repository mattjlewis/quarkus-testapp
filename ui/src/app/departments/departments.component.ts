import { Component, OnInit } from '@angular/core';

import { DepartmentService } from '../department.service';
import { Department } from '../model';

@Component({
  selector: 'app-departments',
  templateUrl: './departments.component.html',
  styleUrls: ['./departments.component.css']
})
export class DepartmentsComponent implements OnInit {
  departments: Array<Department>;

  constructor(private departmentService: DepartmentService) {}

  ngOnInit(): void {
    this.departmentService.getDepartments().subscribe(departments => this.departments = departments);
  }

  add(name: string, location: string): void {
    name = name.trim();
    location = location.trim();
    if (!name) {
      return;
    }
    this.departmentService.createDepartment({ name: name, location: location} as Department)
      .subscribe(department => this.departments.push(department));
  }
  
 
  delete(department: Department): void {
    this.departments = this.departments.filter(dept => dept != department);
    this.departmentService.deleteDepartment(department).subscribe();
  }
}
