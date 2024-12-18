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

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let sessionServiceMock: Partial<SessionService>;
  let routerNavigateSpy: jest.SpyInstance;

  beforeEach(async () => {
    sessionServiceMock = {
      $isLogged: jest.fn(),
      logOut: jest.fn(),
    };

    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      imports: [
        RouterTestingModule,
        MatToolbarModule,
      ],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: AuthService, useValue: {} },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    routerNavigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate');
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should display links for logged users', () => {
    // Mock isLogged to return true
    (sessionServiceMock.$isLogged as jest.Mock).mockReturnValue(of(true));
    fixture.detectChanges();

    const links = fixture.debugElement.queryAll(By.css('.link'));
    const linkTexts = links.map((link) => link.nativeElement.textContent.trim());

    expect(linkTexts).toEqual(['Sessions', 'Account', 'Logout']);
  });

  it('should display links for non-logged users', () => {
    (sessionServiceMock.$isLogged as jest.Mock).mockReturnValue(of(false));
    fixture.detectChanges();

    const links = fixture.debugElement.queryAll(By.css('.link'));
    const linkTexts = links.map((link) => link.nativeElement.textContent.trim());

    expect(linkTexts).toEqual(['Login', 'Register']);
  });

  it('should call sessionService.logOut and navigate to home on logout', () => {
    (sessionServiceMock.$isLogged as jest.Mock).mockReturnValue(of(true));
    fixture.detectChanges();

    const logoutLink = fixture.debugElement.query(By.css('span.link:nth-child(3)')).nativeElement;
    logoutLink.click();

    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(routerNavigateSpy).toHaveBeenCalledWith(['']);
  });

  it('should display "Yoga app" on a primary-colored toolbar', () => {
    const matToolbar = fixture.debugElement.query(By.css('mat-toolbar')).nativeElement;
    expect(matToolbar.getAttribute('color')).toBe('primary');

    const firstSpan = fixture.debugElement.query(By.css('mat-toolbar span')).nativeElement;
    expect(firstSpan.textContent.trim()).toBe('Yoga app');

  });
});
