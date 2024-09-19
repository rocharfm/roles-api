# Comments

## New API Endpoint Approach

<br>

It was asked to extend the project by adding a new endpoint where the user can get a role using `userId` and `teamId` as filters. To do so, after analyzing the existing project controllers I chose the `RolesRestController` to implement the new endpoint, because this controller handles the roles resource exposure.

<br>

_Obs.: When it mentions `Line *` in this section, please refer to the main branch of current repository._

<br>

At a first glance I saw some anti-patterns in this controller:

- The `createRole` controller is replying with `200` status code for the base flow, but it should be `201`.
- Line 33: it is possible to see it is calling a method `CreateRole` which is not following Java code conventions. It should be `createRole`, written in camel case.
- The `getRoles` controller should be a `GET` mapping, not a `POST`. 
- Lines 41-49: there is a logic to build the `List<RolesDto>` to be returned to the client, which is using conventional for loop. It could be using `streams`, which would improve the code readability and would reduce the chances of objects mutability and maintenance efforts.
- The `getRoles` controller may need a pagination implementation.
- The `getRole` by `id` controller should be a `GET` mapping, not a `POST`.

<br>

In order to implement the given task as fast as possible, simulating a real life project, I started creating a new `GET` mapping with the 2 required request parameters (`userId` and `teamId`) and a new method in the roles service to get a role based on the provided combination of `userId` and `teamId`:

commits:
- [controller extension](https://github.com/rocharfm/roles-api/pull/1/commits/569e03f5154bb48cc1c85e2313227b128e180d99)
- [service extension](https://github.com/rocharfm/roles-api/pull/1/commits/a53ea215a504ce5358a4997286e8edccb1c31390)

<br>

After testing it and ensuring the implemented solution was working, I started to do some improvements in the roles controller:

- Changed the status code of the `createRole` base flow from `200` to `201`.
- Changed the `getRoles` from `POST` to a `GET` mapping and renamed it to `getAllRoles`.
- Changed the `getRole` by `id` from `POST` to a `GET` mapping.
- Changed the `getRole` by (`userId`, `teamId`) endpoint path to `/search` to follow to existing project standard from the `MembershipsRestController`.
- Added exception handling for the case when it is provided valid parameters but the database does not contain the data. (`404 not found`)
- Added both unit and integration tests for the new endpoint.
- Fixed the application for the existing tests to pass.

## Project fixes

<br>

- Changed `assignRoleToMembership` base flow status code from `200` to `201`.
- Migrated project database from H2 to mysql.
- Add test containers in order to test against mysql.
- Added exception handling in `getTeam` by `id` controller for the case when it is provided a valid id but the database does not contain the data. (`404 not found`)
- Added exception handling in `assignRoleToMembership` for the case when it is provided a valid `MembershipDto` but the provided user does not belong to the provided team. (`400 bad request`)
- Changed `getMemberships` from `POST` to a `GET` mapping.
- Fixed all project tests.

## TODO

<br>

_Obs.: When it mentions `Line *` in this section, please refer to the dev branch of current repository._

<br>

### General

- The project is done with `Java 11`. Currently, `Java 21` is the LTS. It would be a good practice migrate the project `Java` version from `11` to `21`.
- The project lacks a model and `jpa` entity separation.
- All the services and api interfaces show questionable value at the moment for this project because they are being implemented in the same place where they are being declared. From the technical perspective, they exist to inject dependencies upon abstractions, which is right, but they can be easily extracted using IDE refactoring in case they would be needed.
- There is no clear division between what is business logic and library/framework code. The project would benefit if using a hexagonal architecture approach having better segregation between business logic and controllers.
- There are some hard coded values across the project like http status code and media types which could be replaced by utility classes like `HttpStatus`.

### Models Packages

- All of them (`Team`, `User`, `Membership`, and `Role`) could be rewritten using the new `Java Records` after upgrading the `Java` version.
- Remove wildcard import in `Membership`.

### TeamsClient and UsersClient

- They lack error handling, which means the errors leak to the upper code layers.
- They return the `ResponseEntity` instead of the model, leaving the responsibility of unwrapping the response to the caller of the method.
- There is a possibility of both clients do not find the requested data, what means they should probably be returning an `Optional`.

### Exception Package

- The `ErrorResponse` should be a `dto` of the web package, because it is only used by the `DefaultExceptionHandler`.

### Service Package

#### UsersService Interface

- The `getUser` should return an `Optional`, because it is possible to not find the user.

#### RolesService Interface

- The interface methods are not in camelCase.

#### RolesService Implementation

- It is injecting a `MembershipsService` dependency which is not being used.
- Line 23: there is a hard coded default role definition which could be a project configuration.
- There is a `getDefaultRole` method not being used which should be removed.

#### TeamsService Implementation

- Lines 24 and 28: the reason why it is needed to perform `getBody` on the response entity returned by the client is because the client is not handling its own response, forcing us to handle it on the caller code.

#### UsersService Implementation

- Same `TeamsService Implementation` comment.

### Web Package

#### DTOs

- `fromModel` and `toModel` should be placed in a separated mapper class.
- All DTOs would also be `Java Record` after upgrading the `Java` version.

#### Rest Controllers

- The current `MembershipsRestController` request mapping `/v1/roles/memberships` is questionable. It could be `/v1/memberships` because memberships seems to be an entity.
- `getMemberships` could be refactored to use `stream` and `map` instead of conventional for loop.
- Both `TeamsRestController` endpoints should be a `GET` mapping.
- Both `UsersRestController` endpoints should be a `GET` mapping.

### Application Resources

#### Database Migration

- This should be improved to eliminate the need of providing a valid `id` everytime the client wants to insert a new role and/or membership by defining automatic generation.

#### application.yml

- It is using some hard coded values such as username, password, url, etc. This approach should be replaced by some which uses secrets management service such as aws secrets manager.

### Tests

- The tests are not expected to be public.

#### MembershipsServiceTests

- It is missing get memberships base flow test.

#### RolesServiceTests

- Missing `getRole` by `userId` and `teamId` exception flow tests.
- Missing get all roles base flow test.

#### TeamsServiceTests

- Missing `getTeams` base flow test.
- Missing all exception flow tests.
- Missing `verifyThatUserBelongsToTeam` tests.

#### UsersServiceTests

- Missing all exception flow tests.
- Missing get users base flow test.

### Project Dependencies

- By using the SonarLint IntelliJ plugin it was possible to identify some project dependencies which contain vulnerabilities not fixed yet. Those cases should be handled by trying to replace dependencies and/or versions to remove at least the vulnerabilities classified as high or above. Then all the medium and below should be also removed as best practice.
- There is a h2 dependency which should be removed because it is not being used anymore.
