## Track - Property Listings App
An Android app for tenants to browse rental listings, filter by budget and location and contact agents.

## Download
Latest APK: see **Releases** on the right of this page.

## Features
- Dashboard with total, available, and booked property counts plus a recommended listing
- Search properties by title or neighborhood and filter by monthly rent bracket
- View property details with live KES to USD/EUR rent conversion (with offline caching)
- Add, edit, toggle availability, and delete property listings (saved with Room)
- Call or send a pre-filled SMS viewing request directly to the property agent
- Automated background listing availability checks with WorkManager

## Architecture
ui/           MainActivity, Fragments, and PropertyListAdapter (UI layer)
viewmodel/    PropertyViewModel and PropertyUiState (state management)
domain/       PropertyDomainHelper and PropertyValidator (business & input rules)
data/         PropertyEntity, PropertyDao, AppDatabase, Repositories, and Retrofit API (data layer)

## Team
Name               |   Github Username   |  Role
Brian Koech        |    @fabn3ez         | Team Lead, Room DB & Repository
Daniella Wanjohi   |    @Daniwanjohi     | UI/UX & Dynamic Lists
Baraka Onserio     |    @Default404      | Business Logic, CRUD & Validation
Beyonce Kamure     |    @Kamure          | Navigation & Screen Flow
Bethuel Kilonzo    |    @Kavate1         | Retrofit & Currency API
Ian Siminyu        |    @Iansiminyu      | WorkManager & Tests
