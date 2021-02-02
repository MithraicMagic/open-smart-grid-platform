import {Component, Input, OnInit} from '@angular/core';
import {Device} from "../../../../entities/device";

@Component({
  selector: 'app-authorization-form',
  templateUrl: './authorization-form.component.html',
  styleUrls: ['./authorization-form.component.scss']
})
export class AuthorizationFormComponent implements OnInit {

  @Input() device: Device;

  constructor() { }

  ngOnInit(): void {
  }

}
