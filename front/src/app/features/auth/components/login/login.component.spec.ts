import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { expect } from '@jest/globals';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterTestingModule } from '@angular/router/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { SessionService } from 'src/app/services/session.service';
import { of, throwError } from 'rxjs';
import { LoginRequest } from '../../interfaces/loginRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceMock: Partial<jest.Mocked<AuthService>>;
  let sessionServiceMock: Partial<SessionService>;
  let router: Router;

  beforeEach(async () => {
    authServiceMock = {
      login: jest.fn().mockReturnValue(
        of({
          id: 1,
          username: 'testuser',
          token: 'mockToken',
          type: 'user',
          firstName: 'Test',
          lastName: 'User',
          admin: false,
        } as SessionInformation)
      ),
    };

    sessionServiceMock = { logIn: jest.fn() };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: LoginComponent },
        ]),
        NoopAnimationsModule,
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Unit Tests
  //@unit-test
  it('1️⃣should create the component', () => {
    expect(component).toBeTruthy();
  });
  //@unit-test
  it('1️⃣should initialize the form with empty controls', () => {
    expect(component.form.value).toEqual({ email: '', password: '' });
    expect(component.form.valid).toBe(false);

    const cardTitle = fixture.nativeElement.querySelector('mat-card-title');
    expect(cardTitle.textContent.trim()).toBe('Login');
  });
  //@unit-test
  it('1️⃣should enable the submit button when the form is valid and handle click', () => {
    component.form.setValue({
      email: 'test@example.com',
      password: 'password123',
    });
    fixture.detectChanges();

    const submitButton = fixture.nativeElement.querySelector(
      'button[type="submit"]'
    );
    expect(submitButton.disabled).toBe(false);

    const submitSpy = jest.spyOn(component, 'submit');
    submitButton.click();
    fixture.detectChanges();

    expect(submitSpy).toHaveBeenCalledTimes(1);
  });
  //@unit-test
  it('1️⃣should toggle password visibility on hide-password button click', () => {
    const hidePasswordButton = fixture.nativeElement.querySelector(
      'button[aria-label="Hide password"]'
    );
    const passwordInput = fixture.nativeElement.querySelector(
      'input[formControlName="password"]'
    );

    expect(hidePasswordButton).toBeTruthy();
    expect(passwordInput.getAttribute('type')).toBe('password');

    hidePasswordButton.click();
    fixture.detectChanges();

    expect(passwordInput.getAttribute('type')).toBe('text');

    hidePasswordButton.click();
    fixture.detectChanges();

    expect(passwordInput.getAttribute('type')).toBe('password');
  });
  //@unit-test
  it('1️⃣should display error message if email is invalid', () => {
    component.form.controls['email'].setValue('invalid-email');
    component.form.controls['email'].markAsTouched();
    fixture.detectChanges();

    const emailInput = fixture.nativeElement.querySelector(
      'input[formControlName="email"]'
    );
    expect(emailInput.classList).toContain('ng-invalid');
  });
  // Integration Tests
  //@integrat-test
  it('🔄should submit the form and navigate to sessions on success', () => {
    const mockSessionInformation: SessionInformation = {
      id: 1,
      username: 'testUser',
      token: 'mockToken',
      type: 'user',
      firstName: 'Test',
      lastName: 'User',
      admin: false,
    };

    authServiceMock.login!.mockReturnValue(of(mockSessionInformation));
    component.form.setValue({
      email: 'test@example.com',
      password: 'password123',
    });

    const routerSpy = jest.spyOn(TestBed.inject(Router), 'navigate');
    component.submit();

    expect(authServiceMock.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123',
    });
    expect(sessionServiceMock.logIn).toHaveBeenCalledWith(
      mockSessionInformation
    );
    expect(routerSpy).toHaveBeenCalledWith(['/sessions']);
  });
  //@integrat-test
  it('🔄should display an error message on login failure', () => {
    authServiceMock.login!.mockReturnValue(
      throwError(() => new Error('Login failed'))
    );
    component.form.setValue({
      email: 'test@example.com',
      password: 'wrongpassword',
    });

    component.submit();
    fixture.detectChanges();

    expect(authServiceMock.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'wrongpassword',
    });
    expect(component.onError).toBe(true);

    const errorMessage = fixture.nativeElement.querySelector('p.error');
    expect(errorMessage).toBeTruthy();
    expect(errorMessage.textContent).toContain('An error occurred');
  });
});

// UT : 5/7 = 71%
// IT : 2/7 = 29%
