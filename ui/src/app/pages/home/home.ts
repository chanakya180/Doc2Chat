import { Component, OnInit, signal } from '@angular/core';
import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  username = signal('Hello ');
  constructor(private keycloak: Keycloak) {}

  ngOnInit(): void {
    this.keycloak.loadUserProfile().then((userProfile) => {
      if (userProfile != null) {
        this.username.update(
          (value) =>
            value +
            (
              (userProfile.firstName != null ? userProfile.firstName : '') +
              ' ' +
              (userProfile.lastName != null ? userProfile.lastName : '')
            ).trim(),
        );
      }
    });
  }
}
