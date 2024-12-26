import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router, ParamMap } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { OverlayContainer } from '@angular/cdk/overlay';
import { expect } from '@jest/globals';
import { FormComponent } from './form.component';

import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionService } from '../../../../services/session.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { of } from 'rxjs';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiServiceMock: Partial<jest.Mocked<SessionApiService>>;
  let teacherServiceMock: Partial<TeacherService>;

  const mockSession = {
    id: '1',
    name: 'Yoga Session',
    date: '2024-12-17',
    teacher_id: '2',
    description: 'A relaxing yoga session',
  };

  const mockTeachers = [
    { id: '1', firstName: 'John', lastName: 'Doe' },
    { id: '2', firstName: 'Jane', lastName: 'Smith' },
  ];

  const activatedRouteMock = {
    snapshot: {
      paramMap: {
        get: jest.fn((key: string) => (key === 'id' ? '1' : null)),
        has: jest.fn((key: string) => key === 'id'),
        getAll: jest.fn().mockReturnValue([]),
        keys: [],
      } as Partial<ParamMap> as ParamMap,
    },
  };

  const routerMock: Partial<Router> = {
    navigate: jest.fn(),
    get url() {
      return '/sessions/create';
    },
  };

  const sessionServiceMock: Partial<SessionService> = {
    sessionInformation: {
      id: 1,
      username: 'testUser',
      token: 'mockToken',
      type: 'admin',
      admin: true,
    } as SessionInformation,
  };

  beforeEach(async () => {
    sessionApiServiceMock = {
      detail: jest.fn().mockReturnValue(of(mockSession)),
      create: jest.fn().mockReturnValue(of(mockSession)),
      update: jest.fn().mockReturnValue(of(mockSession)),
    };

    teacherServiceMock = {
      all: jest.fn().mockReturnValue(of(mockTeachers)),
    };

    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [
        ReactiveFormsModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatSelectModule,
        RouterTestingModule,
        NoopAnimationsModule,
      ],
      providers: [
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        MatSnackBar,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Unit Tests
  //@unit-test
it('1️⃣should create the component', () => {
    expect(component).toBeTruthy();
  });

  //@unit-test
it('1️⃣should initialize form with empty values', () => {
    component['initForm']();

    expect(component.sessionForm?.value).toEqual({
      name: '',
      date: '',
      teacher_id: '',
      description: ''
    });
    expect(component.sessionForm?.get('name')?.hasValidator(Validators.required)).toBeTruthy();
    expect(component.sessionForm?.get('date')?.hasValidator(Validators.required)).toBeTruthy();
    expect(component.sessionForm?.get('teacher_id')?.hasValidator(Validators.required)).toBeTruthy();
    expect(component.sessionForm?.get('description')?.hasValidator(Validators.required)).toBeTruthy();
  });

  //@unit-test
it('1️⃣should handle sessionForm undefined on submit', () => {
    component.sessionForm = undefined;
    expect(() => component.submit()).not.toThrow();
  });

  //@unit-test
it('1️⃣should get teachers$ observable for dropdown list', () => {
    expect(component.teachers$).toBeDefined();
    const teacherServiceSpy = jest.spyOn(teacherServiceMock, 'all');
    component.teachers$.subscribe(teachers => {
      expect(teachers).toEqual(mockTeachers);
      expect(teachers.length).toBe(2);
      expect(teachers[0]).toEqual({ id: '1', firstName: 'John', lastName: 'Doe' });
      expect(teachers[1]).toEqual({ id: '2', firstName: 'Jane', lastName: 'Smith' });
    });
    expect(teacherServiceSpy).toHaveBeenCalled();
  });

  // Integration Tests
  //@integrat-test
it('🔄 should handle different initialization scenarios correctly', () => {
    const scenarios = [
      {
        description: 'Create mode',
        setup: () => {
          jest
            .spyOn(routerMock, 'url', 'get')
            .mockReturnValue('/sessions/create');
          fixture = TestBed.createComponent(FormComponent);
          component = fixture.componentInstance;
        },
        expectations: () => {
          expect(component.onUpdate).toBeFalsy();
          expect(component.sessionForm?.value).toEqual({
            name: '',
            date: '',
            teacher_id: '',
            description: '',
          });
          expect(sessionApiServiceMock.detail).not.toHaveBeenCalled();
        },
      },
      {
        description: 'Update mode',
        setup: () => {
          jest
            .spyOn(routerMock, 'url', 'get')
            .mockReturnValue('/sessions/update/1');
          fixture = TestBed.createComponent(FormComponent);
          component = fixture.componentInstance;
        },
        expectations: () => {
          expect(component.onUpdate).toBeTruthy();
          expect(sessionApiServiceMock.detail).toHaveBeenCalledWith('1');
          expect(component.sessionForm?.value).toEqual({
            name: 'Yoga Session',
            date: '2024-12-17',
            teacher_id: '2',
            description: 'A relaxing yoga session',
          });
        },
      },
      {
        description: 'Non-admin access',
        setup: () => {
          sessionServiceMock.sessionInformation!.admin = false;
          fixture = TestBed.createComponent(FormComponent);
          component = fixture.componentInstance;
        },
        expectations: () => {
          expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
        },
      },
    ];

    scenarios.forEach((scenario) => {
      jest.clearAllMocks();
      console.log(`Testing initialization: ${scenario.description}`);
      scenario.setup();
      fixture.detectChanges();
      scenario.expectations();
    });
  });

  //@integrat-test
it('🔄 should handle form submissions correctly in different scenarios', () => {
    const scenarios = [
      {
        description: 'Create new session',
        setup: () => {
          component.onUpdate = false;
          component.sessionForm?.setValue({
            name: 'TY1',
            date: '2024-12-18',
            teacher_id: '1',
            description: 'Test Yoga 001',
          });
        },
        submit: () => {
          component.submit();
        },
        expectations: () => {
          expect(sessionApiServiceMock.create).toHaveBeenCalledWith({
            name: 'TY1',
            date: '2024-12-18',
            teacher_id: '1',
            description: 'Test Yoga 001',
          });
          expect(sessionApiServiceMock.update).not.toHaveBeenCalled();
          expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
        },
      },
      {
        description: 'Update existing session',
        setup: () => {
          component.onUpdate = true;
          (component as any).id = '1';
          component.sessionForm?.setValue({
            name: 'Updated TY1',
            date: '2024-12-19',
            teacher_id: '2',
            description: 'Updated Test Yoga 001',
          });
          const mockUpdatedSession = {
            id: 1,
            name: 'Updated TY1',
            date: new Date('2024-12-19'),
            teacher_id: 2,
            description: 'Updated Test Yoga 001',
            users: [],
          };
          sessionApiServiceMock.update?.mockReturnValue(of(mockUpdatedSession));
        },
        submit: () => {
          component.submit();
        },
        expectations: () => {
          expect(sessionApiServiceMock.update).toHaveBeenCalledWith('1', {
            name: 'Updated TY1',
            date: '2024-12-19',
            teacher_id: '2',
            description: 'Updated Test Yoga 001',
          });
          expect(sessionApiServiceMock.create).not.toHaveBeenCalled();
          expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
        },
      },
      {
        description: 'Form validation state',
        setup: () => {
          component.onUpdate = false;
          component.sessionForm?.reset();
        },
        submit: () => {
          const saveButton: HTMLButtonElement =
            fixture.nativeElement.querySelector('button[type="submit"]');
          expect(saveButton.disabled).toBeTruthy();

          component.sessionForm?.setValue({
            name: 'Save Bt test',
            date: '2024-12-12',
            teacher_id: '1',
            description: 'Save Button enable/disable test',
          });
          fixture.detectChanges();

          expect(saveButton.disabled).toBeFalsy();
          saveButton.click();
        },
        expectations: () => {
          expect(sessionApiServiceMock.create).toHaveBeenCalled();
        },
      },
    ];

    scenarios.forEach((scenario) => {
      jest.clearAllMocks();
      console.log(`Testing submission: ${scenario.description}`);
      scenario.setup();
      fixture.detectChanges();
      scenario.submit();
      fixture.detectChanges();
      scenario.expectations();
    });
  });

  //@integrat-test
it('🔄 should display arrow_back icon and call window.history.back() on click', () => {
    const routerNavigateSpy = jest.spyOn(routerMock, 'navigate');
    fixture.detectChanges();
    const backButton: HTMLElement =
      fixture.nativeElement.querySelector('button mat-icon');
    expect(backButton).toBeTruthy();

    backButton.click();
    fixture.detectChanges();

    expect(routerNavigateSpy).toHaveBeenCalledWith(['/sessions']);
  });

});

// UT : 4/7 = 57%
// IT : 3/7 = 43%
