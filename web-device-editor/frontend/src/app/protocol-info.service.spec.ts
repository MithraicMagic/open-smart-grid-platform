import { TestBed } from '@angular/core/testing';

import { ProtocolInfoService } from './protocol-info.service';

describe('ProtocolInfoService', () => {
  let service: ProtocolInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProtocolInfoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
