export const interceptLoginUserSuccess = () => {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      token: 'mockJwtToken',
      id: 1,
      username: 'userName',
      firstName: 'firstName',
      lastName: 'lastName',
      admin: true,
    },
  }).as('loginSuccess');
};

export const interceptLoginFailWrongPassword = () => {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 401,
    body: { message: 'Invalid password' },
  }).as('loginFail');
};

export const interceptLoginFailUnknownUser = () => {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 404,
    body: { message: 'User not found' },
  }).as('unknownUser');
};

export const interceptFetchSessionsEmpty = () => {
  cy.intercept('GET', '/api/session', {
    statusCode: 200,
    body: [],
  }).as('fetchSessions');
};

export const interceptRegisterSuccess = () => {
  cy.intercept('POST', '/api/auth/register', {
    statusCode: 201,
    body: { message: 'User created successfully' },
  }).as('registerSuccess');
};

export const interceptRegisterEmailExists = () => {
  cy.intercept('POST', '/api/auth/register', {
    statusCode: 400,
    body: { message: 'Email already exists' },
  }).as('registerEmailExists');
};

export const interceptRegisterBadRequest = () => {
  cy.intercept('POST', '/api/auth/register', {
    statusCode: 400,
    body: { message: 'An error occurred' },
  }).as('badRequest');
};

export const interceptLogin = (email, role) => {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      token: 'mock-jwt-token',
      type: 'Bearer',
      id: 1,
      username: email,
      firstName: role === 'admin' ? 'Test' : 'User',
      lastName: role.toUpperCase(),
      admin: role === 'admin',
    },
  }).as('loginRequest');
  cy.wrap('mock-jwt-token').as('mockJwtToken');
};

export const interceptUserProfile = (role, email) => {
  cy.get('@mockJwtToken').then((token) => {
    cy.intercept(
      {
        method: 'GET',
        url: '/api/user/me',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
      {
        statusCode: 200,
        body: {
          firstName: role === 'admin' ? 'Test' : 'User',
          lastName: role.toUpperCase(),
          email,
          admin: role === 'admin',
        },
      }
    ).as('profileRequest');
  });
};

export const interceptUserSessions = () => {
  cy.get('@mockJwtToken').then((token) => {
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
      {
        statusCode: 200,
        body: [
          {
            id: 1,
            name: 'Yoga Session',
            date: '2025-01-01',
          },
          {
            id: 2,
            name: 'Meditation Session',
            date: '2025-01-02',
          },
        ],
      }
    ).as('sessionsRequest');
  });
};

export const interceptUserProfil = () => {
  cy.intercept(
    {
      method: 'GET',
      url: '/api/user/me',
    },
    {
      statusCode: 200,
      body: {
        firstName: 'Test',
        lastName: 'Test',
        email: 'this@ema.il',
        admin: true,
      },
    }
  ).as('adminProfile');
};
