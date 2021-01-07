import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DeviceEditorComponent} from "./device-editor/device-editor.component";
import {DeviceListComponent} from "./device-list/device-list.component";

const routes: Routes = [
  { path: '', component: DeviceListComponent },
  { path: 'device/:id', component: DeviceEditorComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
