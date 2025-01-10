import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { expect } from '@jest/globals';
import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';
import { Observable } from 'rxjs';

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

  // Unit Tests
  //@unit-test
it('1️⃣should be created', () => {
    expect(service).toBeTruthy();
  });

  //@unit-test
it('1️⃣should return an array of sessions for all()', () => {
    service.all().subscribe((sessions) => {
      expect(sessions).toBeInstanceOf(Array);
      expect(sessions[0]).toEqual(mockSession1);
    });
    const req = httpMock.expectOne('api/session');
    req.flush(mockSessions);
  });

  //@unit-test
it('1️⃣should return a session for detail()', () => {
    service.detail('1').subscribe((session) => {
      expect(session).toBeInstanceOf(Object);
      expect(session).toEqual(mockSession1);
    });
    const req = httpMock.expectOne('api/session/1');
    req.flush(mockSession1);
  });

  // Integration Tests

  describe('🔄 API Call Scenarios', () => {
    const scenarios: {
      description: string;
      method: () => Observable<any>;
      expectedUrl: string;
      expectedMethod: string;
      expectedBody: any;
    }[] = [
      {//@integrat-test
        description: 'should create a session',
        method: () => service.create(mockSession1),
        expectedUrl: 'api/session',
        expectedMethod: 'POST',
        expectedBody: mockSession1,
      },
      {//@integrat-test
        description: 'should delete a session',
        method: () => service.delete('1'),
        expectedUrl: 'api/session/1',
        expectedMethod: 'DELETE',
        expectedBody: null,
      },
      {//@integrat-test
        description: 'should update a session',
        method: () =>
          service.update('1', { ...mockSession1, name: 'Updated Session' }),
        expectedUrl: 'api/session/1',
        expectedMethod: 'PUT',
        expectedBody: { ...mockSession1, name: 'Updated Session' },
      },
      {//@integrat-test
        description: 'should participate in a session',
        method: () => service.participate('1', '10'),
        expectedUrl: 'api/session/1/participate/10',
        expectedMethod: 'POST',
        expectedBody: null,
      },
      {//@integrat-test
        description: 'should unParticipate from a session',
        method: () => service.unParticipate('1', '10'),
        expectedUrl: 'api/session/1/participate/10',
        expectedMethod: 'DELETE',
        expectedBody: null,
      },
    ];

    scenarios.forEach((scenario) => {
      it(scenario.description, () => {
        scenario.method().subscribe();
        const req = httpMock.expectOne(scenario.expectedUrl);
        expect(req.request.method).toBe(scenario.expectedMethod);
        expect(req.request.body).toEqual(scenario.expectedBody);
      });
    });
  });
});

// UT : 3/8 = 37%
// IT : 5/8 = 63%
