import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GpsFormComponent } from './gps-form.component';

describe('GpsFormComponent', () => {
  let component: GpsFormComponent;
  let fixture: ComponentFixture<GpsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GpsFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GpsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
