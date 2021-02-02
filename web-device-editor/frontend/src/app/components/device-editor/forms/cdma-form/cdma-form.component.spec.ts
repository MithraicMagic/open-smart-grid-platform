import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CdmaFormComponent } from './cdma-form.component';

describe('CdmaFormComponent', () => {
  let component: CdmaFormComponent;
  let fixture: ComponentFixture<CdmaFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CdmaFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CdmaFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
