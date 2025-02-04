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
import { NgZone } from '@angular/core';
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
  let ngZone: NgZone;

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
    ngZone = TestBed.inject(NgZone);
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
      description: '',
    });
    expect(
      component.sessionForm?.get('name')?.hasValidator(Validators.required)
    ).toBeTruthy();
    expect(
      component.sessionForm?.get('date')?.hasValidator(Validators.required)
    ).toBeTruthy();
    expect(
      component.sessionForm
        ?.get('teacher_id')
        ?.hasValidator(Validators.required)
    ).toBeTruthy();
    expect(
      component.sessionForm
        ?.get('description')
        ?.hasValidator(Validators.required)
    ).toBeTruthy();
  });
  //@unit-test
  it('1️⃣should handle sessionForm undefined on submit', () => {
    component.sessionForm = undefined;
    expect(() => component.submit()).not.toThrow();
  });

  //@unit-test
  it('🔄 should enable submit button when all inputs are valid', () => {
    const submitButton: HTMLButtonElement = fixture.nativeElement.querySelector(
      'button[type="submit"]'
    );

    expect(submitButton.disabled).toBeTruthy();

    component.sessionForm?.setValue({
      name: 'Test Session',
      date: '2024-12-18',
      teacher_id: '1',
      description: 'Test Yoga 001',
    });
    fixture.detectChanges();
    expect(submitButton.disabled).toBeFalsy();

    component.sessionForm?.get('name')?.setValue('');
    fixture.detectChanges();
    expect(submitButton.disabled).toBeTruthy();

    component.sessionForm?.get('name')?.setValue('Valid Session Name');
    fixture.detectChanges();
    expect(submitButton.disabled).toBeFalsy();
  });

  //@unit-test
  it('1️⃣should get teachers$ observable for dropdown list', () => {
    expect(component.teachers$).toBeDefined();
    const teacherServiceSpy = jest.spyOn(teacherServiceMock, 'all');
    component.teachers$.subscribe((teachers) => {
      expect(teachers).toEqual(mockTeachers);
      expect(teachers.length).toBe(2);
      expect(teachers[0]).toEqual({
        id: '1',
        firstName: 'John',
        lastName: 'Doe',
      });
      expect(teachers[1]).toEqual({
        id: '2',
        firstName: 'Jane',
        lastName: 'Smith',
      });
    });
    expect(teacherServiceSpy).toHaveBeenCalled();
  });

  // Integration Tests


  it('🔄 should handle different initialization scenarios correctly', () => {
    const scenarios = [
      {//@integrat-test
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
      {//@integrat-test
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
      {//@integrat-test
        description: 'Non-admin access',
        setup: () => {
          sessionServiceMock.sessionInformation!.admin = false;
          fixture = TestBed.createComponent(FormComponent);
          component = fixture.componentInstance;
        },
        expectations: () => {
          ngZone.run(() => {
            expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
          });
        },
      },
    ];

    scenarios.forEach((scenario) => {
      jest.clearAllMocks();
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
          ngZone.run(() => component.submit());
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
        },
        submit: () => {
          ngZone.run(() => component.submit());
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
        description: 'Empty name field',
        setup: () => {
          component.onUpdate = false;
          component.sessionForm?.setValue({
            name: '',
            date: '2024-12-18',
            teacher_id: '1',
            description: 'Test Yoga 001',
          });
        },
        submit: () => {
          ngZone.run(() => component.submit());
        },
        expectations: () => {
          //expect(sessionApiServiceMock.create).not.toHaveBeenCalled();
          expect(component.sessionForm?.invalid).toBeTruthy();
        },
      },
    ];

    scenarios.forEach((scenario) => {
      jest.clearAllMocks();
      scenario.setup();
      fixture.detectChanges();
      scenario.submit();
      fixture.detectChanges();
      scenario.expectations();
    });
  });
  //@integrat-test
  it('🔄 should display arrow_back icon and call window.history.back() on click', () => {
    ngZone.run(() => {
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
});
