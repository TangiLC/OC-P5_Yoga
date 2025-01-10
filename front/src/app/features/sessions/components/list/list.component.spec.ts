import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { ListComponent } from './list.component';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { of } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionServiceMock: any;
  let sessionApiServiceMock: any;

  // Données de test
  const mockSessions = [
    {
      id: 1,
      name: 'Yoga Session',
      date: '2024-12-17',
      description: 'Morning yoga class',
    },
    {
      id: 2,
      name: 'Meditation',
      date: '2024-12-18',
      description: 'Evening meditation session',
    },
  ];

  const mockSessionInformation = {
    id: 1,
    admin: true, // Tester pour un utilisateur admin
  };

  beforeEach(async () => {
    sessionApiServiceMock = {
      all: jest.fn().mockReturnValue(of(mockSessions)),
    };

    sessionServiceMock = {
      sessionInformation: mockSessionInformation,
    };

    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        RouterTestingModule,
      ],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: SessionApiService, useValue: sessionApiServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Unit Tests
  //@unit-test
it('1️⃣ should create the component', () => {
    expect(component).toBeTruthy();
  });

  //@unit-test
it('1️⃣ should fetch sessions on initialization', () => {
    expect(sessionApiServiceMock.all).toHaveBeenCalled();
    component.sessions$.subscribe((sessions) => {
      expect(sessions).toEqual(mockSessions);
    });
  });

  // Integration Tests
//@integrat-test
  describe('🔄 Button display logic', () => {
    const scenarios = [
      {
        description: 'should display "Create" button if user is admin',
        admin: true,
        buttonSelector: 'button[routerLink="create"]',
        expectedCount: 1,
      },
      {
        description: 'should not display "Create" button if user is not admin',
        admin: false,
        buttonSelector: 'button[routerLink="create"]',
        expectedCount: 0,
      },
      {
        description:
          'should display "Edit" buttons for sessions if user is admin',
        admin: true,
        buttonSelector: 'button[ng-reflect-router-link*="update"]',
        expectedCount: 2,
      },
      {
        description: 'should configure "Detail" buttons for all sessions',
        admin: true,
        buttonSelector: 'button[ng-reflect-router-link*="detail"]',
        expectedCount: 2,
      },
    ];

    scenarios.forEach((scenario) => {
      it(scenario.description, () => {
        sessionServiceMock.sessionInformation.admin = scenario.admin;
        fixture.detectChanges();

        const buttons = fixture.debugElement.queryAll(
          By.css(scenario.buttonSelector)
        );
        expect(buttons.length).toBe(scenario.expectedCount);
      });
    });
  });

  //@integrat-test
it('🔄 should render session details', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const sessionTitles = compiled.querySelectorAll('mat-card-title');
    const sessionSubtitles = compiled.querySelectorAll('mat-card-subtitle');

    expect(sessionTitles.length).toBe(2 + 1); // +1 for 'Sessions available'
    expect(sessionTitles[0].textContent).toContain('Sessions available');
    expect(sessionTitles[1].textContent).toContain('Yoga Session');
    expect(sessionTitles[2].textContent).toContain('Meditation');

    expect(sessionSubtitles[0].textContent).toContain('December 17, 2024');
    expect(sessionSubtitles[1].textContent).toContain('December 18, 2024');
  });
});

// UT : 2/4 = 50%
// IT : 2/4 = 50%
