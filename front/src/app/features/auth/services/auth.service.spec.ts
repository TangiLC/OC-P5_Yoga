import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { expect } from '@jest/globals';
import { AuthService } from './auth.service';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { NgZone } from '@angular/core';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let ngZone: NgZone;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    ngZone = TestBed.inject(NgZone);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // Unit Tests
  //@unit-test
  it('1️⃣should be created', () => {
    expect(service).toBeTruthy();
  });

  // Integration Tests
  describe('register', () => {
    //@integrat-test
    it('🔄should send a POST request to register a user', () => {
      const mockRegisterRequest: RegisterRequest = {
        firstName: 'Test',
        lastName: 'MOCK',
        password: 'password123!',
        email: 'testuser@example.com',
      };
      ngZone.run(() => {
        service.register(mockRegisterRequest).subscribe();

        const req = httpMock.expectOne('api/auth/register');
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(mockRegisterRequest);
        req.flush(null);
      });
    });
  });

  describe('login', () => {
    //@integrat-test
    it('🔄should send a POST request to login a user', () => {
      const mockLoginRequest: LoginRequest = {
        email: 'testuser@example.com',
        password: 'password123!',
      };

      const mockSessionInformation: SessionInformation = {
        id: 1,
        username: 'testuser',
        token: 'mockToken',
        type: 'user',
        firstName: 'Test',
        lastName: 'User',
        admin: false,
      };

      service.login(mockLoginRequest).subscribe((response) => {
        expect(response).toEqual(mockSessionInformation);
      });

      const req = httpMock.expectOne('api/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockLoginRequest);
      req.flush(mockSessionInformation);
    });
  });
});

// UT : 1/3 = 33%
// IT : 2/3 = 67%
