import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { of } from 'rxjs';

import { MeComponent } from './me.component';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  let userServiceMock: Partial<UserService>;
  let routerMock: Partial<Router>;
  let snackBarMock: Partial<MatSnackBar>;

  const mockSessionService = {
    sessionInformation: { id: 1, admin: true },
    logOut: jest.fn(), // Jest mock
  };

  const mockUser = {
    id: 1,
    firstName: 'Test',
    lastName: 'TEST',
    email: 'test.test@test.com',
    admin: false,
    password: '123456',
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  beforeEach(async () => {
    // Mocks Jest
    userServiceMock = {
      getById: jest.fn().mockReturnValue(of(mockUser)),
      delete: jest.fn().mockReturnValue(of({})),
    };

    routerMock = {
      navigate: jest.fn(),
    };

    snackBarMock = {
      open: jest.fn(),
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: userServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: MatSnackBar, useValue: snackBarMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch user data on init', () => {
    expect(userServiceMock.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(mockUser);
  });

  it('should call window.history.back() when back() is invoked', () => {
    const historyBackSpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(historyBackSpy).toHaveBeenCalled();
  });

  it('should delete the user and log out', () => {
    component.delete();

    expect(userServiceMock.delete).toHaveBeenCalledWith('1');
    expect(snackBarMock.open).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 }
    );
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should display user information in the template', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const paragraphs = compiled.querySelectorAll('p');
    const longDate = new Date().toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });

    expect(paragraphs[0].textContent).toContain('Name: Test TEST');
    expect(paragraphs[1].textContent).toContain('Email: test.test@test.com');
    // <p> 2 is either 'You are admin' or 'Delete your account', tested later
    expect(paragraphs[2].textContent).toBeTruthy();
    expect(paragraphs[3].textContent).toContain(`Created at:  ${longDate}`);
    expect(paragraphs[4].textContent).toContain(`Last update:  ${longDate}`);
  });

  it('should display "Delete" button if user is not admin', () => {
    component.user = { ...mockUser, admin: false };
    fixture.detectChanges();

    const deleteButton = fixture.debugElement.query(
      By.css('button[color="warn"]')
    );
    expect(deleteButton).toBeTruthy();
  });

  it('should not display "Delete" button if user is admin', () => {
    component.user = { ...mockUser, admin: true };
    fixture.detectChanges();

    const deleteButton = fixture.debugElement.query(
      By.css('button[color="warn"]')
    );
    expect(deleteButton).toBeNull();
  });
});
