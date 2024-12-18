import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { expect } from '@jest/globals';
import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const mockTeacher1: Teacher = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    createdAt:new Date('2020-10-10'),
    updatedAt:new Date('2021-11-11')
  };

  const mockTeacher2: Teacher = {
    id: 2,
    firstName: 'Jane',
    lastName: 'Smith',
    createdAt:new Date('2021-11-11'),
    updatedAt:new Date('2022-12-12')
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

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all teachers', () => {
    service.all().subscribe((teachers) => {
      expect(teachers).toEqual(mockTeachers);
      expect(teachers.length).toBe(2);
    });

    const req = httpMock.expectOne('api/teacher');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeachers);
  });

  it('should fetch teacher details by ID', () => {
    service.detail('1').subscribe((teacher) => {
      expect(teacher).toEqual(mockTeacher1);
    });

    const req = httpMock.expectOne('api/teacher/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeacher1);
  });
});
