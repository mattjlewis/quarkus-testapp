import { Component, OnInit } from '@angular/core';

import { Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

import { Department } from '../model';
import { DepartmentService } from '../department.service';

@Component({
  selector: 'app-department-search',
  templateUrl: './department-search.component.html',
  styleUrls: ['./department-search.component.css']
})
export class DepartmentSearchComponent implements OnInit {
  departments$: Observable<Array<Department>>;
  private searchTerms = new Subject<string>();

  constructor(private departmentService: DepartmentService) { }

  ngOnInit(): void {
    this.departments$ = this.searchTerms.pipe(
      // wait 300ms after each keystroke before considering the term
      debounceTime(300),

      // ignore new term if same as previous term
      distinctUntilChanged(),

      // switch to new search observable each time the term changes
      switchMap((term: string) => this.departmentService.searchDepartments(term)),
    );
  }

  // Push a search term into the observable stream.
  search(term: string): void {
    this.searchTerms.next(term);
  }
}
