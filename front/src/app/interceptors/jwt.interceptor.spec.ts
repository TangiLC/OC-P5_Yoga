import { expect } from '@jest/globals';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpRequest, HttpHandler, HttpResponse } from '@angular/common/http';
import { JwtInterceptor } from './jwt.interceptor';
import { SessionService } from '../services/session.service';
import { of } from 'rxjs';

describe('JwtInterceptor', () => {
  let interceptor: JwtInterceptor;
  let sessionService: Partial<SessionService>;
  let httpHandler: HttpHandler;

  beforeEach(() => {
    sessionService = {
      isLogged: false,
      sessionInformation: undefined,
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        JwtInterceptor,
        { provide: SessionService, useValue: sessionService },
      ],
    });

    interceptor = TestBed.inject(JwtInterceptor);

    httpHandler = {
      handle: jest.fn((req) => of(req)) as any,
    };
  });

  afterEach(() => {
    jest.clearAllMocks();
  });
  // Unit Tests
  //@unit-test
  it('1️⃣should not add Authorization header if user is not logged in', () => {
    const request = new HttpRequest('GET', '/api/test');
    sessionService.isLogged = false;

    interceptor.intercept(request, httpHandler).subscribe();

    expect(httpHandler.handle).toHaveBeenCalledWith(request);
    expect(request.headers.has('Authorization')).toBe(false);
  });
  //@unit-test
  it('1️⃣should add Authorization header with token if user is logged in', () => {
    const request = new HttpRequest('GET', '/api/test');
    sessionService.isLogged = true;
    sessionService.sessionInformation = {
      token: 'fake-jwt-token',
      type: 'user',
      id: 1,
      username: 'TestMOCK',
      firstName: 'Test',
      lastName: 'MOCK',
      admin: false,
    };

    interceptor.intercept(request, httpHandler).subscribe();

    const modifiedRequest = (httpHandler.handle as jest.Mock).mock.calls[0][0];

    expect(modifiedRequest.headers.get('Authorization')).toBe(
      'Bearer fake-jwt-token'
    );
    expect(httpHandler.handle).toHaveBeenCalledTimes(1);
  });
  //@unit-test
  it('1️⃣should pass the original request through the interceptor chain', () => {
    const request = new HttpRequest('GET', '/api/test');
    const response = of(
      new HttpResponse({ status: 200, body: { data: 'response' } })
    );
    jest.spyOn(httpHandler, 'handle').mockReturnValue(response);

    const result = interceptor.intercept(request, httpHandler);

    result.subscribe((response) => {
      expect(response).toEqual({ data: 'response' });
    });

    expect(httpHandler.handle).toHaveBeenCalledWith(request);
  });
});
