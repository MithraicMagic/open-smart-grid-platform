import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {DeviceListComponent} from './components/device-list/device-list.component';
import {DeviceEditorComponent} from './components/device-editor/device-editor.component';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {HeaderComponent} from './components/header/header.component';
import {GenericFormComponent} from './components/device-editor/forms/generic-form/generic-form.component';
import {AddressFormComponent} from './components/device-editor/forms/address-form/address-form.component';
import {GpsFormComponent} from './components/device-editor/forms/gps-form/gps-form.component';
import {CdmaFormComponent} from './components/device-editor/forms/cdma-form/cdma-form.component';
import {StatusFormComponent} from './components/device-editor/forms/status-form/status-form.component';
import {AuthorizationFormComponent} from './components/device-editor/forms/authorization-form/authorization-form.component';

@NgModule({
  declarations: [
    AppComponent,
    DeviceListComponent,
    DeviceEditorComponent,
    HeaderComponent,
    GenericFormComponent,
    AddressFormComponent,
    GpsFormComponent,
    CdmaFormComponent,
    StatusFormComponent,
    AuthorizationFormComponent
  ],
    imports: [
        BrowserModule,
        ReactiveFormsModule,
        HttpClientModule,
        AppRoutingModule,
        CommonModule,
        FormsModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
