import { expect } from '@jest/globals';
import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { UserService } from './user.service';
import { User } from '../interfaces/user.interface';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser: User = {
    id: 1,
    firstName: 'Alice',
    lastName: 'Johnson',
    password: 'pass123',
    createdAt:new Date('2024-12-12'),
    updatedAt:new Date('2024-12-13'),
    email: 'alice.johnson@example.com',
    admin: true,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService],
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Vérifie qu'aucune requête HTTP n'est en attente
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch user details by ID', () => {
    service.getById('1').subscribe((user) => {
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser); // Simule la réponse HTTP avec les données mockées
  });

  it('should delete a user by ID', () => {
    service.delete('1').subscribe((response) => {
      expect(response).toBeTruthy(); // Vérifie que la suppression retourne une réponse
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({}); // Simule une réponse HTTP vide
  });
});
