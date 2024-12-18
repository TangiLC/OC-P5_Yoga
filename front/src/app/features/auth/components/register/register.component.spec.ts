import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegisterRequest } from '../../interfaces/registerRequest.interface';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceMock: Partial<jest.Mocked<AuthService>>;
  let routerMock: Partial<Router>;

  beforeEach(async () => {
    authServiceMock = {
      register: jest.fn(),
    };

    routerMock = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        HttpClientTestingModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        NoopAnimationsModule,
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty values', () => {
    expect(component.form.value).toEqual({
      email: '',
      firstName: '',
      lastName: '',
      password: '',
    });
    expect(component.form.valid).toBe(false);

    const cardTitle = fixture.nativeElement.querySelector('mat-card-title');
    expect(cardTitle.textContent.trim()).toBe('Register');

    const submitButton = fixture.nativeElement.querySelector(
      'button[type="submit"]'
    );
    expect(submitButton.disabled).toBe(true);
  });

  it('should enable the submit button when the form is valid', () => {
    component.form.setValue({
      email: 'test@example.com',
      firstName: 'Test',
      lastName: 'User',
      password: 'password123',
    });
    fixture.detectChanges();

    const submitButton = fixture.nativeElement.querySelector(
      'button[type="submit"]'
    );
    expect(submitButton.disabled).toBe(false);
  });

  it('should submit the form and navigate to login on success', () => {
    const mockRegisterRequest: RegisterRequest = {
      email: 'test@example.com',
      firstName: 'Test',
      lastName: 'User',
      password: 'password123',
    };

    authServiceMock.register!.mockReturnValue(of(void 0));
    component.form.setValue(mockRegisterRequest);

    const submitSpy = jest.spyOn(component, 'submit');
    component.submit();

    expect(submitSpy).toHaveBeenCalledTimes(1);
    expect(authServiceMock.register).toHaveBeenCalledWith(mockRegisterRequest);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should display an error message on registration failure', () => {
    authServiceMock.register!.mockReturnValue(throwError(() => new Error('Registration failed')));
    component.form.setValue({
      email: 'test@example.com',
      firstName: 'Test',
      lastName: 'User',
      password: 'password123',
    });

    component.submit();
    fixture.detectChanges();

    expect(component.onError).toBe(true);
    const errorMessage = fixture.nativeElement.querySelector('span.error');
    expect(errorMessage).toBeTruthy();
    expect(errorMessage.textContent).toContain('An error occurred');
  });

  it('should render placeholders for all inputs', () => {
    const firstNameInput = fixture.nativeElement.querySelector(
      'input[formcontrolname="firstName"]'
    );
    expect(firstNameInput.getAttribute('data-placeholder')).toBe('First name');
    const lastNameInput = fixture.nativeElement.querySelector(
      'input[formcontrolname="lastName"]'
    );
    expect(lastNameInput.getAttribute('data-placeholder')).toBe('Last name');
    const emailInput = fixture.nativeElement.querySelector(
      'input[formcontrolname="email"]'
    );
    expect(emailInput.getAttribute('data-placeholder')).toBe('Email');
    const passwordInput = fixture.nativeElement.querySelector(
      'input[formcontrolname="password"]'
    );
    expect(passwordInput.getAttribute('data-placeholder')).toBe('Password');

  });
});
