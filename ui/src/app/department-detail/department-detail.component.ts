import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

import { DepartmentService } from '../department.service';
import { Department } from '../model';

@Component({
  selector: 'app-department-detail',
  templateUrl: './department-detail.component.html',
  styleUrls: ['./department-detail.component.css']
})
export class DepartmentDetailComponent implements OnInit {

  department: Department;

  constructor(
    private route: ActivatedRoute,
    private location: Location,
    private departmentService: DepartmentService) { }

  ngOnInit(): void {
    this.getDepartment();
  }

  getDepartment(): void {
    const id = +this.route.snapshot.paramMap.get('id');
    this.departmentService.getDepartment(id).subscribe(department => this.department = department);
  }

  goBack(): void {
    this.location.back();
  }

  save(): void {
   this.departmentService.updateDepartment(this.department)
     .subscribe(() => this.goBack());
  }
}
