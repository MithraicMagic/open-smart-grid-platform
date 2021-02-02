import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DeviceEditorComponent} from "./components/device-editor/device-editor.component";
import {DeviceListComponent} from "./components/device-list/device-list.component";

const routes: Routes = [
  { path: '', component: DeviceListComponent },
  { path: 'device/:id', component: DeviceEditorComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
