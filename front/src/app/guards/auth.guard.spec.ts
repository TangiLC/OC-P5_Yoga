import { expect } from '@jest/globals';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { SessionService } from '../services/session.service';

describe('AuthGuard', () => {
  let MockGuard: AuthGuard;
  let sessionService: Partial<SessionService>;
  let routerMock: Partial<Router>;

  beforeEach(() => {
    routerMock = {
      navigate: jest.fn(),
    };

    sessionService = {
      isLogged: false,
    };

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionService },
      ],
    });

    MockGuard = TestBed.inject(AuthGuard);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });
  //Unit Tests
  describe('canActivate', () => {
    //@unit-test
    it('1️⃣should return true for a logged user', () => {
      sessionService.isLogged = true;

      const result = MockGuard.canActivate();

      expect(result).toBe(true);
      expect(routerMock.navigate).not.toHaveBeenCalled();
    });
    //@unit-test
    it('1️⃣should return false and navigate to /login for a not-logged user', () => {
      sessionService.isLogged = false;

      const result = MockGuard.canActivate();

      expect(result).toBe(false);
      expect(routerMock.navigate).toHaveBeenCalledWith(['login']);
      expect(routerMock.navigate).toHaveBeenCalledTimes(1);
    });
  });
});

// UT : 2/2 = 100%
