import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';
import { DetailComponent } from './detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionService } from '../../../../services/session.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from '../../../../interfaces/teacher.interface';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

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
    users: [1, 2],
    createdAt: new Date('2024-12-01'),
    updatedAt: new Date('2024-12-15'),
  };

  const mockTeacher: Partial<Teacher> = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
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

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch session and teacher details on init', () => {
    expect(sessionApiServiceMock.detail).toHaveBeenCalledWith('1');
    expect(teacherServiceMock.detail).toHaveBeenCalledWith('1');
    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
  });

  it('should display the session name in titlecase', () => {
    const title = fixture.nativeElement.querySelector('h1');
    expect(title.textContent).toContain('Yoga Session');
  });

  it('should display delete button and handle delete action for admin only', fakeAsync(() => {
    sessionServiceMock.sessionInformation!.admin = false;
    fixture.detectChanges();
    let deleteButton = fixture.nativeElement.querySelector(
      'button[color="warn"]'
    );
    expect(deleteButton.offsetParent).toBeNull(); //offset => hidden

    sessionServiceMock.sessionInformation!.admin = true;
    fixture.detectChanges();

    deleteButton = fixture.nativeElement.querySelector('button[color="warn"]');
    expect(deleteButton).toBeTruthy();

    jest.spyOn(sessionApiServiceMock, 'delete').mockReturnValue(of({}));
    const routerNavigateSpy = jest.spyOn(routerMock, 'navigate');

    deleteButton.click();
    tick(3001);
    fixture.detectChanges();

    expect(sessionApiServiceMock.delete).toHaveBeenCalledWith('1');
    expect(routerNavigateSpy).toHaveBeenCalledWith(['sessions']);
  }));

  it('should display Participate button for not enrolled user and handle click', () => {
    sessionServiceMock.sessionInformation!.admin = true;
    component.isParticipate = true;
    fixture.detectChanges();

    let participateButton = fixture.nativeElement.querySelector(
      'button[color="primary"]'
    );
    expect(participateButton).toBeNull();

    component.isAdmin = false;
    fixture.detectChanges();
    participateButton = fixture.nativeElement.querySelector(
      'button[color="primary"]'
    );
    expect(participateButton).toBeNull();

    component.isParticipate = false;
    fixture.detectChanges();
    participateButton = fixture.nativeElement.querySelector(
      'button[color="primary"]'
    );
    expect(participateButton).toBeTruthy();
    participateButton.click();

    expect(sessionApiServiceMock.participate).toHaveBeenCalledWith('1', '1');
    expect(sessionApiServiceMock.detail).toHaveBeenCalled();
  });

  it('should display unParticipate button for enrolled user and handle click', () => {
    component.isAdmin = true;
    component.isParticipate = false;
    fixture.detectChanges();
    let unParticipateButton = fixture.nativeElement.querySelector(
      'button[color="warn"]'
    );
    expect(unParticipateButton).toBeTruthy();
    expect(
      unParticipateButton.querySelector('span.ml1')?.textContent.trim()
    ).toBe('Delete');

    component.isAdmin = false;
    fixture.detectChanges();
    unParticipateButton = fixture.nativeElement.querySelector(
      'button[color="warn"]'
    );
    expect(unParticipateButton).toBeNull();

    component.isParticipate = true;
    fixture.detectChanges();
    unParticipateButton = fixture.nativeElement.querySelector(
      'button[color="warn"]'
    );
    expect(unParticipateButton).toBeTruthy();
    expect(
      unParticipateButton.querySelector('span.ml1')?.textContent.trim()
    ).toBe('Do not participate');
    unParticipateButton.click();
    fixture.detectChanges();
    expect(sessionApiServiceMock.unParticipate).toHaveBeenCalledWith('1', '1');
    expect(sessionApiServiceMock.detail).toHaveBeenCalled();
  });

  it('should call window.history.back on back button click', () => {
    const backSpy = jest.spyOn(window.history, 'back');
    const backButton = fixture.nativeElement.querySelector(
      'button[mat-icon-button]'
    );
    backButton.click();

    expect(backSpy).toHaveBeenCalled();
  });
});
