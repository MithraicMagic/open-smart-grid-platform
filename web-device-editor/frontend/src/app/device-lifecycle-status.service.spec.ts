import { TestBed } from '@angular/core/testing';

import { DeviceLifecycleStatusService } from './device-lifecycle-status.service';

describe('DeviceLifecycleStatusService', () => {
  let service: DeviceLifecycleStatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DeviceLifecycleStatusService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
