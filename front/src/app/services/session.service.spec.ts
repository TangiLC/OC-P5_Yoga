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

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have isLogged initialized as false', () => {
    expect(service.isLogged).toBe(false);
  });

  it('should have sessionInformation initialized as undefined', () => {
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit isLogged changes via $isLogged observable', (done) => {
    service
      .$isLogged()
      .pipe(take(1))
      .subscribe((isLogged) => {
        expect(isLogged).toBe(service.isLogged);
        done();
      });
    service.logIn(mockUser);
  });

  it('should set isLogged to true and update sessionInformation on logIn', () => {
    service.logIn(mockUser);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockUser);
  });

  it('should emit true via $isLogged on logIn', (done) => {
    service.logIn(mockUser);

    service
      .$isLogged()
      .pipe(take(1))
      .subscribe({
        next: (isLogged) => {
          expect(isLogged).toBe(true);
          done();
        },
        error: (err) => done(err),
      });

    service.logOut();
  });

  it('should set isLogged to false and clear sessionInformation on logOut', () => {
    service.logIn(mockUser);
    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockUser);
    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit false via $isLogged on logOut', (done) => {
    service.logIn(mockUser);

    service
      .$isLogged()
      .pipe(skip(1), take(1))
      .subscribe({
        next: (isLogged) => {
          expect(isLogged).toBe(false);
          done();
        },
        error: (err) => done(err),
      });

    service.logOut();
  });

  it('should call next() when logIn is called', () => {
    const nextSpy = jest.spyOn(service as any, 'next'); // Espionne la méthode privée `next`
    service.logIn(mockUser);

    expect(nextSpy).toHaveBeenCalled();
  });

  it('should call next() when logOut is called', () => {
    const nextSpy = jest.spyOn(service as any, 'next'); // Espionne la méthode privée `next`
    service.logOut();

    expect(nextSpy).toHaveBeenCalled();
  });
});
