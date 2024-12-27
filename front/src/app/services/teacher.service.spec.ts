import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { expect } from '@jest/globals';
import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';
import { Observable } from 'rxjs';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const mockTeacher1: Teacher = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    createdAt: new Date('2020-10-10'),
    updatedAt: new Date('2021-11-11'),
  };

  const mockTeacher2: Teacher = {
    id: 2,
    firstName: 'Jane',
    lastName: 'Smith',
    createdAt: new Date('2021-11-11'),
    updatedAt: new Date('2022-12-12'),
  };

  const mockTeachers: Teacher[] = [mockTeacher1, mockTeacher2];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService],
    });

    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // Unit Tests
  //@unit-test
it('1️⃣should be created', () => {
    expect(service).toBeTruthy();
  });

  //@unit-test
it('1️⃣should correctly define API endpoints', () => {
    const basePath = service['pathService'];
    expect(basePath).toBe('api/teacher');

    const teacherId = '123';
    const detailPath = `${basePath}/${teacherId}`;
    expect(detailPath).toBe('api/teacher/123');
  });

  // Integration Tests
  //@integrat-test
it('🔄should perform API calls to fetch all teachers /or teacher by ID', () => {
    const scenarios: {
      description: string;
      method: () => Observable<Teacher | Teacher[]>;
      expectedUrl: string;
      mockResponse: Teacher | Teacher[];
      validate: any;
    }[] = [
      {
        description: 'fetch all teachers',
        method: () => service.all(),
        expectedUrl: 'api/teacher',
        mockResponse: mockTeachers,
        validate: (response: Teacher[]) => {
          expect(response).toEqual(mockTeachers);
          expect(response.length).toBe(2);
        },
      },
      {
        description: 'fetch teacher details by ID',
        method: () => service.detail('1'),
        expectedUrl: 'api/teacher/1',
        mockResponse: mockTeacher1,
        validate: (response: Teacher) => {
          expect(response).toEqual(mockTeacher1);
        },
      },
    ];

    scenarios.forEach((scenario) => {
      scenario.method().subscribe((response) => {
        scenario.validate(response);
      });

      const req = httpMock.expectOne(scenario.expectedUrl);
      expect(req.request.method).toBe('GET');
      req.flush(scenario.mockResponse);
    });
  });
});

// UT : 2/3 = 67%
// IT : 1/3 = 33%
