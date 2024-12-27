import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { NotFoundComponent } from './not-found.component';
import { By } from '@angular/platform-browser';

describe('NotFoundComponent', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotFoundComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Unit Tests (UT)
  //@unit-test
  it('1️⃣should create', () => {
    expect(component).toBeTruthy();
  });
  //@unit-test
  it('1️⃣should have a container with class "flex justify-center mt3"', () => {
    const container = fixture.debugElement.query(
      By.css('.flex.justify-center.mt3')
    );
    expect(container).toBeTruthy();
  });

  // Integration Tests (IT)
  //@integrat-test
  it('🔄should render "Page not found !" in an <h1> tag', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const h1 = compiled.querySelector('h1');
    expect(h1).toBeTruthy();
    expect(h1?.textContent).toContain('Page not found !');
  });
});

// UT : 2/3 = 66%
// IT : 1/3 = 33%
