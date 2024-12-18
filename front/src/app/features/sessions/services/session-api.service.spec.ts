import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { expect } from '@jest/globals';
import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSession1: Session = {
    id: 1,
    name: 'Yoga 1st Session',
    description: 'A relaxing yoga mock 1',
    date: new Date('2024-12-15'),
    teacher_id: 2,
    users: [2, 4, 6, 8],
  };

  const mockSession2: Session = {
    id: 2,
    name: 'Yoga Session',
    description: 'A dynamic yoga mock 2',
    date: new Date('2024-12-20'),
    teacher_id: 1,
    users: [],
  };

  const mockSessions: Array<Session> = [mockSession1, mockSession2];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService],
    });

    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all sessions', () => {
    service.all().subscribe((sessions) => {
      expect(sessions).toEqual(mockSessions);
      expect(sessions.length).toBe(2);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions);
  });

  it('should fetch session details by ID', fakeAsync(() => {
    let sessionDetail: Session | undefined;
    service.detail('1').subscribe((session) => {
      sessionDetail = session;
    });
    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockSession1);
    tick();
    expect(sessionDetail).toEqual(mockSession1);
  }));

  it('should delete a session by ID', fakeAsync(() => {
    let sessions = [mockSession1, mockSession2];

    service.delete('1').subscribe(() => {
      //mock deleting session
      sessions = sessions.filter((session) => session.id !== 1);

      expect(sessions.length).toBe(1);
      expect(sessions).toEqual([mockSession2]);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});

    tick(3001);
  }));

  it('should create a new session', fakeAsync(() => {
    let sessions: Session[] = [mockSession1];

    service.create(mockSession2).subscribe((createdSession) => {
      // Mock post session
      sessions = [...sessions, createdSession];

      expect(sessions.length).toBe(2);
      expect(sessions).toEqual([mockSession1, mockSession2]);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockSession2);
    req.flush(mockSession2);

    tick(3001);
  }));

  it('should update a session by ID', fakeAsync(() => {
    let sessions: Session[] = mockSessions;
    const updatedSession1 = {
      ...mockSession1,
      name: 'Updated Yoga 1st Session',
    };

    service.update('1', updatedSession1).subscribe((response) => {
      // Mock update session
      sessions = sessions.map((session) =>
        session.id === updatedSession1.id ? updatedSession1 : session
      );
      expect(sessions.length).toBe(2);
      expect(sessions).toEqual([updatedSession1, mockSession2]);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedSession1);
    req.flush(updatedSession1);
    tick(3001);
  }));

  it('should participate in a session', fakeAsync(() => {
    let session = { ...mockSession1 };

    service.participate('1', '10').subscribe(() => {
      // Mock post user
      session.users = [...session.users, 10];

      expect(session.users.length).toBe(5);
      expect(session.users).toContain(10);
    });

    const req = httpMock.expectOne('api/session/1/participate/10');
    expect(req.request.method).toBe('POST');
    req.flush({});

    tick(3001);
  }));

  it('should unParticipate from a session', fakeAsync(() => {
    let session = { ...mockSession1 };

    service.unParticipate('1', '8').subscribe(() => {
      //Mock user delete
      session.users = session.users.filter((userId) => userId !== 8);

      expect(session.users.length).toBe(3);
      expect(session.users).not.toContain(8);
    });

    const req = httpMock.expectOne('api/session/1/participate/8');
    expect(req.request.method).toBe('DELETE');
    req.flush({});

    tick(3001);
  }));
});
