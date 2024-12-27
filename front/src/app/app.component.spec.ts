import { expect } from '@jest/globals';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { SessionService } from './services/session.service';
import { AuthService } from './features/auth/services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { NgZone } from '@angular/core';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let sessionServiceMock: Partial<SessionService>;
  let routerNavigateSpy: jest.SpyInstance;
  let ngZone: NgZone;

  beforeEach(async () => {
    sessionServiceMock = {
      $isLogged: jest.fn(),
      logOut: jest.fn(),
    };

    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      imports: [RouterTestingModule, MatToolbarModule],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: AuthService, useValue: {} },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    routerNavigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate');
    ngZone = TestBed.inject(NgZone);
    fixture.detectChanges();
  });

  //@unit-test
  it('1️⃣should create the component', () => {
    expect(component).toBeTruthy();
  });

  //@unit-test
  it('1️⃣ should logOut and navigate to home when logout() is called', () => {
    ngZone.run(() => {
      component.logout();
      expect(sessionServiceMock.logOut).toHaveBeenCalled();
      expect(routerNavigateSpy).toHaveBeenCalledWith(['']);
    });
  });

  //@integrat-test
  it('🔄should display the links based on login status', () => {
    ngZone.run(() => {
      const scenarios = [
        {
          description: 'logged users',
          isLogged: true,
          expectedLinks: ['Sessions', 'Account', 'Logout'],
        },
        {
          description: 'non-logged users',
          isLogged: false,
          expectedLinks: ['Login', 'Register'],
        },
      ];

      scenarios.forEach((scenario) => {
        (sessionServiceMock.$isLogged as jest.Mock).mockReturnValue(
          of(scenario.isLogged)
        );
        fixture.detectChanges();

        const links = fixture.debugElement.queryAll(By.css('.link'));
        const linkTexts = links.map((link) =>
          link.nativeElement.textContent.trim()
        );
        expect(linkTexts).toEqual(scenario.expectedLinks);
      });
    });
  });

  //@integrat-test
  it('🔄should display "Yoga app" on a primary-colored toolbar', () => {
    ngZone.run(() => {
      const matToolbar = fixture.debugElement.query(
        By.css('mat-toolbar')
      ).nativeElement;
      expect(matToolbar.getAttribute('color')).toBe('primary');

      const firstSpan = fixture.debugElement.query(
        By.css('mat-toolbar span')
      ).nativeElement;
      expect(firstSpan.textContent.trim()).toBe('Yoga app');
    });
  });
});
