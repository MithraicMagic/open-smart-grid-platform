import { TestBed } from '@angular/core/testing';

import { DeviceFunctionGroupService } from './device-function-group.service';

describe('DeviceFunctionGroupService', () => {
  let service: DeviceFunctionGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DeviceFunctionGroupService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
