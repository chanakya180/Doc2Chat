import { Component, OnInit } from '@angular/core';
import Keycloak from 'keycloak-js';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  username: string | undefined;
  constructor(private keycloak: Keycloak) {}

  ngOnInit(): void {
    this.keycloak.loadUserProfile().then((userProfile) => {
      if (userProfile) {
        this.username =
          'Hello ' + userProfile.firstName + ' ' + userProfile.lastName;
      }
    });
  }
}
