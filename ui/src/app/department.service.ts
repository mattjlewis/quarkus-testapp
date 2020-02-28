import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { MessageService } from './message.service';
import { Department } from './model';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {
  private static readonly APPLICATION_JSON = 'application/json';
  private static readonly DEPARTMENT_URL = 'http://localhost:9090/rest/department';  // URL to web api
  private static readonly UPDATE_HTTP_OPTIONS = {
    headers: new HttpHeaders({ 'Content-Type': DepartmentService.APPLICATION_JSON })
  };

  constructor(
    private http: HttpClient,
    private messageService: MessageService) { }

  /** GET all departments */
  getDepartments(): Observable<Array<Department>> {
    this.log('Fetching departments...');
    return this.http.get<Array<Department>>(DepartmentService.DEPARTMENT_URL)
      .pipe(
        tap(_ => this.log('Fetched departments')),
        catchError(this.handleError<Array<Department>>('getDepartments', [])));
  }

  /** GET a department by id. Will get 404 if id not found */
  getDepartment(id: number): Observable<Department> {
    this.log(`Fetching department ${id}...`);
    return this.http.get<Department>(`${DepartmentService.DEPARTMENT_URL}/${id}`)
      .pipe(
        tap(_ => this.log(`Fetched department ${id}`)),
        catchError(this.handleError<Department>(`getDepartment id=${id}`)));
  }

  /** POST: Create a department on the server */
  createDepartment(department: Department): Observable<Department> {
    this.log(`Creating department ${department.name}...`);
    return this.http.post<Department>(DepartmentService.DEPARTMENT_URL, department, DepartmentService.UPDATE_HTTP_OPTIONS)
      .pipe(
        tap((newDepartment: Department) => this.log(`Created department ${department.name}, id: ${newDepartment.id}`)),
        catchError(this.handleError<any>(`createDepartment name=${department.name}`))
      );
  }

  /** PUT: Update the department on the server */
  updateDepartment(department: Department): Observable<any> {
    this.log(`Updating department ${department.id}...`);
    this.log(JSON.stringify(department));
    return this.http.put<Department>(DepartmentService.DEPARTMENT_URL, department, DepartmentService.UPDATE_HTTP_OPTIONS)
      .pipe(
        tap(_ => this.log(`Updated department ${department.id}`)),
        catchError(this.handleError<Department>(`updateDepartment id=${department.id}`))
      );
  }

  /** DELETE: delete the hero from the server */
  deleteDepartment(department: Department | number): Observable<Department> {
    const id = typeof department === 'number' ? department : department.id;

    return this.http.delete<Department>(`${DepartmentService.DEPARTMENT_URL}/${id}`).pipe(
      tap(_ => this.log(`Deleted department ${id}`)),
      catchError(this.handleError<Department>('deleteDepartment'))
    );
  }

  /** Log a DepartmentService message with the MessageService */
  private log(message: string) {
    this.messageService.add(`DepartmentService: ${message}`);
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

}
