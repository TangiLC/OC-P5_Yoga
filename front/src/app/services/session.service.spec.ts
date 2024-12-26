import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { skip, take } from 'rxjs';

describe('SessionService', () => {
  let service: SessionService;

  const mockUser: SessionInformation = {
    id: 1,
    username: 'testUser',
    firstName: 'Test',
    lastName: 'MOCK',
    token: 'mockToken',
    type: 'admin',
    admin: true,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SessionService],
    });

    service = TestBed.inject(SessionService);
  });

  // Unit Tests (1️⃣)
  it('1️⃣ should be created', () => {
    expect(service).toBeTruthy();
  });

  it('1️⃣ should have isLogged initialized as false', () => {
    expect(service.isLogged).toBe(false);
  });

  it('1️⃣ should have sessionInformation initialized as undefined', () => {
    expect(service.sessionInformation).toBeUndefined();
  });

  it('1️⃣ should set isLogged to true and update sessionInformation on logIn', () => {
    service.logIn(mockUser);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockUser);
  });

  it('1️⃣ should set isLogged to false and clear sessionInformation on logOut', () => {
    service.logIn(mockUser);
    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockUser);
    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('1️⃣ should call next() when logIn is called', () => {
    const nextSpy = jest.spyOn(service as any, 'next'); // Espionne la méthode privée `next`
    service.logIn(mockUser);

    expect(nextSpy).toHaveBeenCalled();
  });

  it('1️⃣ should call next() when logOut is called', () => {
    const nextSpy = jest.spyOn(service as any, 'next'); // Espionne la méthode privée `next`
    service.logOut();

    expect(nextSpy).toHaveBeenCalled();
  });

  // Integration Tests (🔄)

  it('🔄 should emit true/false values via $isLogged on logIn/logOut', (done) => {
    const scenarios = [
      {
        action: () => service.logIn(mockUser),
        expectedValue: true,
      },
      {
        action: () => service.logOut(),
        expectedValue: false,
      },
      {
        action: () => service.logIn({...mockUser,id:2}),
        expectedValue: true,
      },
    ];
    let emissionCount = 0;
    service
      .$isLogged()
      .pipe(skip(1), take(scenarios.length))
      .subscribe({
        next: (isLogged) => {
          expect(isLogged).toBe(scenarios[emissionCount].expectedValue);
          emissionCount++;
          if (emissionCount === scenarios.length) {
            done();
          }
        },
        error: (err) => done(err),
      });

    scenarios.forEach((scenario) => scenario.action());
  });
});

// UT : 7/8 = 88%
// IT : 1/8 = 12%
