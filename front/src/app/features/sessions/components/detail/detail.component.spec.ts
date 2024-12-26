import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';
import { expect } from '@jest/globals';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionService } from '../../../../services/session.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from '../../../../interfaces/teacher.interface';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;

  let sessionApiServiceMock: Partial<SessionApiService>;
  let teacherServiceMock: Partial<TeacherService>;
  let sessionServiceMock: Partial<SessionService> = {
    isLogged: true,
    sessionInformation: {
      id: 1,
      admin: true,
      token: 'mock-token',
      type: 'user',
      username: 'testAdmin',
      firstName: 'Admin',
      lastName: 'Test',
    } as SessionInformation,

    $isLogged: jest.fn().mockReturnValue(of(true)),
    logIn: jest.fn(),
    logOut: jest.fn(),
  };

  let routerMock: Partial<Router>;

  const mockSession: Session = {
    id: 1,
    name: 'yoga session',
    date: new Date('2024-12-17'),
    teacher_id: 1,
    description: 'A relaxing yoga session',
    users: [1, 2, 3],
    createdAt: new Date('2024-12-01'),
    updatedAt: new Date('2024-12-15'),
  };

  const mockTeacher: Teacher = {
    id: 1,
    firstName: 'Teacher',
    lastName: 'MOCK',
    createdAt: new Date('2020-12-01'),
    updatedAt: new Date('2022-12-01'),
  };

  beforeEach(async () => {
    sessionApiServiceMock = {
      detail: jest.fn().mockReturnValue(of(mockSession)),
      delete: jest.fn().mockReturnValue(of({})),
      participate: jest.fn().mockReturnValue(of({})),
      unParticipate: jest.fn().mockReturnValue(of({})),
    };

    teacherServiceMock = {
      detail: jest.fn().mockReturnValue(of(mockTeacher)),
    };

    routerMock = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      declarations: [DetailComponent],
      imports: [
        MatSnackBarModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        ReactiveFormsModule,
        NoopAnimationsModule,
      ],
      providers: [
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router, useValue: routerMock },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => '1' } } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Unit Tests
  it('1️⃣should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('1️⃣should display the session name in titlecase', () => {
    const title = fixture.nativeElement.querySelector('h1');
    expect(title.textContent).toContain('Yoga Session');
  });

  it('1️⃣should call window.history.back on back button click', () => {
    const backSpy = jest.spyOn(window.history, 'back');
    const backButton = fixture.nativeElement.querySelector(
      'button[mat-icon-button]'
    );
    backButton.click();

    expect(backSpy).toHaveBeenCalled();
  });

  // Integration Tests
  it('🔄should fetch session and teacher details on init', () => {
    expect(sessionApiServiceMock.detail).toHaveBeenCalledWith('1');
    expect(teacherServiceMock.detail).toHaveBeenCalledWith('1');
    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
  });

  it('🔄should display button and handle click based on role and participation', fakeAsync(() => {
    // Test scenarios
    const scenarios = [
      {
        description: 'Admin user (Delete button)',
        setup: () => {
          sessionServiceMock.sessionInformation!.admin = true;
          component.isAdmin = true;
          component.isParticipate = false;
        },
        expectations: () => {
          const deleteButton = fixture.nativeElement.querySelector(
            'button[color="warn"]'
          );
          expect(deleteButton).toBeTruthy();
          expect(
            deleteButton.querySelector('span.ml1')?.textContent.trim()
          ).toBe('Delete');
          deleteButton.click();
          tick(3001);
          expect(sessionApiServiceMock.delete).toHaveBeenCalledWith('1');
          expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
        },
      },
      {
        description: 'Non-enrolled user (Participate button)',
        setup: () => {
          sessionServiceMock.sessionInformation!.admin = false;
          component.isAdmin = false;
          component.isParticipate = false;
        },
        expectations: () => {
          const participateButton = fixture.nativeElement.querySelector(
            'button[color="primary"]'
          );
          expect(participateButton).toBeTruthy();
          participateButton.click();
          expect(sessionApiServiceMock.participate).toHaveBeenCalledWith(
            '1',
            '1'
          );
          expect(sessionApiServiceMock.detail).toHaveBeenCalled();

          const deleteButton = fixture.nativeElement.querySelector(
            'button[color="warn"]'
          );
          expect(deleteButton?.textContent?.includes('Delete')).toBeFalsy();
        },
      },
      {
        description: 'Enrolled user (Unparticipate button)',
        setup: () => {
          sessionServiceMock.sessionInformation!.admin = false;
          component.isAdmin = false;
          component.isParticipate = true;
        },
        expectations: () => {
          const unParticipateButton = fixture.nativeElement.querySelector(
            'button[color="warn"]'
          );
          expect(unParticipateButton).toBeTruthy();
          expect(
            unParticipateButton.querySelector('span.ml1')?.textContent.trim()
          ).toBe('Do not participate');
          unParticipateButton.click();
          expect(sessionApiServiceMock.unParticipate).toHaveBeenCalledWith(
            '1',
            '1'
          );
          expect(sessionApiServiceMock.detail).toHaveBeenCalled();
          const participateButton = fixture.nativeElement.querySelector(
            'button[color="primary"]'
          );
          expect(participateButton).toBeNull();
        },
      },
    ];

    scenarios.forEach((scenario) => {
      jest.clearAllMocks();
      scenario.setup();
      fixture.detectChanges();
      scenario.expectations();
    });
  }));

  it('🔄should render data correctly in the template', () => {
    component.session = mockSession;
    component.teacher = mockTeacher;

    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;

    const infos = Array.from(compiled.querySelectorAll('.ml1'));
    expect(infos[1].textContent?.trim()).toBe('Teacher MOCK');
    expect(infos[2].textContent?.trim()).toBe('3 attendees');
    expect(infos[3].textContent?.trim()).toBe('December 17, 2024');

    expect(compiled.querySelector('.created')?.textContent?.trim()).toBe(
      'Created at:  December 1, 2024'
    );
    expect(compiled.querySelector('.updated')?.textContent?.trim()).toBe(
      'Last update:  December 15, 2024'
    );
  });
});

// UT : 3/6 = 50%
// IT : 3/6 = 50%
