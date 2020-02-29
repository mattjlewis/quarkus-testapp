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

  add(dName: string, dLocation: string): void {
    dName = dName.trim();
    dLocation = dLocation.trim();
    if (!dName) {
      return;
    }
    this.departmentService.createDepartment({ name: dName, location: dLocation } as Department)
      .subscribe(department => this.departments.push(department));
  }

  delete(department: Department): void {
    this.departments = this.departments.filter(dept => dept !== department);
    this.departmentService.deleteDepartment(department).subscribe();
  }
}
