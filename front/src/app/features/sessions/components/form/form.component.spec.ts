import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormComponent } from './form.component';
import { ActivatedRoute, Router, ParamMap } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { OverlayContainer } from '@angular/cdk/overlay';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionService } from '../../../../services/session.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { of } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

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

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect non-admin users', () => {
    sessionServiceMock.sessionInformation!.admin = false;
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should display arrow_back icon and call window.history.back() on click', () => {
    const routerNavigateSpy  = jest.spyOn(routerMock, 'navigate');
    fixture.detectChanges();
    const backButton: HTMLElement = fixture.nativeElement.querySelector('button mat-icon');
    expect(backButton).toBeTruthy();

    backButton.click();
    fixture.detectChanges();

    expect(routerNavigateSpy).toHaveBeenCalledWith(['/sessions']);
  });

  it('should initialize in update mode if url param "/update"', () => {
    jest.spyOn(routerMock, 'url', 'get').mockReturnValue('/sessions/update/1');

    // Act
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    // Assert
    expect(component.onUpdate).toBeTruthy();
    expect(sessionApiServiceMock.detail).toHaveBeenCalledWith('1');
    expect(component.sessionForm?.value).toEqual({
      name: 'Yoga Session',
      date: '2024-12-17',
      teacher_id: '2',
      description: 'A relaxing yoga session',
    });
  });

  it('should initialize create mode without url param "/update"', () => {
    jest.spyOn(routerMock, 'url', 'get').mockReturnValue('/sessions/create');

    // Act
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    // Assert
    expect(component.onUpdate).toBeFalsy();
    expect(component.sessionForm?.value).toEqual({
      name: '',
      date: '',
      teacher_id: '',
      description: '',
    });
  });

  it('should initialize form in create mode', () => {
    expect(component.onUpdate).toBeFalsy();
    expect(component.sessionForm?.value).toEqual({
      name: '',
      date: '',
      teacher_id: '',
      description: '',
    });
  });

  it('should submit the form and create a session', () => {
    component.sessionForm?.setValue({
      name: 'TY1',
      date: '2024-12-18',
      teacher_id: '1',
      description: 'Test Yoga 001',
    });
    component.submit();
    fixture.detectChanges();

    expect(sessionApiServiceMock.create).toHaveBeenCalledWith({
      name: 'TY1',
      date: '2024-12-18',
      teacher_id: '1',
      description: 'Test Yoga 001',
    });
    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
    const h1Element: HTMLElement = fixture.nativeElement.querySelector('h1');
    expect(h1Element).toBeTruthy();
    expect(h1Element.textContent).toContain('Create session');
  });

  it('should submit the form and update a session', () => {
    const mockUpdatedSession = {
      id: 1,
      name: 'Updated TY1',
      date: new Date('2024-12-19'),
      teacher_id: 2,
      description: 'Updated Test Yoga 001',
      users: [],
    };
    component.onUpdate = true;
    (component as any).id = '1';

    component.sessionForm?.setValue({
      name: 'Updated TY1',
      date: '2024-12-19',
      teacher_id: '2',
      description: 'Updated Test Yoga 001',
    });

    sessionApiServiceMock.update?.mockReturnValue(of(mockUpdatedSession));

    component.submit();
    fixture.detectChanges();

    expect(sessionApiServiceMock.update).toHaveBeenCalledWith('1', {
      name: 'Updated TY1',
      date: '2024-12-19',
      teacher_id: '2',
      description: 'Updated Test Yoga 001',
    });

    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
    const h1Element: HTMLElement = fixture.nativeElement.querySelector('h1');
    expect(h1Element).toBeTruthy();
    expect(h1Element.textContent).toContain('Update session');
  });

  it('should display all teachers in the dropdown list', () => {
    (component as any).teachers$ = of(mockTeachers);
    fixture.detectChanges();
    //console.log(fixture.nativeElement.outerHTML);
    const matSelect: HTMLElement = fixture.nativeElement.querySelector('mat-select');
    expect(matSelect).toBeTruthy();

    matSelect.click();
    fixture.detectChanges();
    const overlayContainerElement = TestBed.inject(OverlayContainer).getContainerElement();
    const options:NodeListOf<HTMLElement> = overlayContainerElement.querySelectorAll('.mat-option-text');

    expect(options.length).toBe(2);
    expect(options[0].textContent).toContain('John Doe');
    expect(options[1].textContent).toContain('Jane Smith');
  });

  it('should display a disable save button for empty form and enable submit when filled', () => {
    const saveButton: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(saveButton).toBeTruthy();
    const submitSpy = jest.spyOn(component, 'submit');
    fixture.detectChanges();
    expect(saveButton.disabled).toBeTruthy();

    saveButton.click();
    fixture.detectChanges();
    expect(submitSpy).not.toHaveBeenCalled();

    component.sessionForm?.setValue({
      name: 'Save Bt test',
      date: '2024-12-12',
      teacher_id: '1',
      description: 'Save Button enable/disable test',
    });

    fixture.detectChanges();
    expect(saveButton.disabled).toBeFalsy();
    saveButton.click();
    fixture.detectChanges();
    expect(submitSpy).toHaveBeenCalled();
  });

});
