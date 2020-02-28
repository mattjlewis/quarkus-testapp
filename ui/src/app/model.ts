export interface Department {
  id?: number;
  name: string;
  location: string;
  version?: number;
  created?: Date;
  lastUpdated?: Date;
  lastUpdatedBy?: string;
  employees?: Array<Employee>;
}

export interface Employee {
  id?: number;
  name: string;
  emailAddress: string;
  favouriteDrink: string;
}
