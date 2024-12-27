import { expect } from '@jest/globals';
import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { UserService } from './user.service';
import { User } from '../interfaces/user.interface';
import { Observable } from 'rxjs';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser: User = {
    id: 1,
    firstName: 'Alice',
    lastName: 'Johnson',
    password: 'pass123',
    createdAt: new Date('2024-12-12'),
    updatedAt: new Date('2024-12-13'),
    email: 'alice.johnson@example.com',
    admin: true,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService],
    });

    service = TestBed.inject(UserService);
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
    expect(basePath).toBe('api/user');

    const userId = '456';
    const detailPath = `${basePath}/${userId}`;
    expect(detailPath).toBe('api/user/456');
  });

  // Integration Tests
  //@integrat-test
it('🔄should handle user-related API calls', () => {
    const scenarios: {
      description: string;
      method: () => Observable<User>;
      expectedUrl: string;
      expectedMethod: string;
      mockResponse: User | {};
      validate: any;
    }[] = [
      {
        description: 'fetch user details by ID',
        method: () => service.getById('1'),
        expectedUrl: 'api/user/1',
        expectedMethod: 'GET',
        mockResponse: mockUser,
        validate: (response: User) => {
          expect(response).toEqual(mockUser);
        },
      },
      {
        description: 'delete user by ID',
        method: () => service.delete('1'),
        expectedUrl: 'api/user/1',
        expectedMethod: 'DELETE',
        mockResponse: {},
        validate: (response: any) => {
          expect(response).toBeTruthy();
        },
      },
    ];
    scenarios.forEach((scenario) => {
      scenario.method().subscribe((response) => {
        scenario.validate(response);
      });
      const req = httpMock.expectOne(scenario.expectedUrl);
      expect(req.request.method).toBe(scenario.expectedMethod);
      req.flush(scenario.mockResponse);
    });
  });
});

// UT : 2/3 = 67%
// IT : 1/3 = 33%
