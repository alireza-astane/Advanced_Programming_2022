# Requirements checklist

Taken from the two project briefs in `coursework/Project`. `[x]` means built. The server side of each item is covered by tests over real sockets, and every screen is opened for every role by the client tests. `[-]` means deliberately left out, with the reason.

## Accounts
- [x] Login with username, password and a four digit picture captcha that can be refreshed and changes after a failure
- [x] Passwords stored as hashes
- [x] Password change forced after more than three hours away, with no way past it
- [x] Demo data: every role at least twice, five departments with two courses each, students with courses
- [x] Head of department exists only through the initial data

## Home and profile
- [x] Home shows current time, exit, last login, photo, name and email, and reaches every page
- [x] Students also see status, supervisor, registration permission and registration time
- [x] Deputy can register a new student or professor with username and password
- [x] Student and professor profiles with all listed fields and a default photo
- [x] Email and phone can be changed
- [x] Every user can change the theme

## Registration
- [x] Course list with at least three filters
- [x] Professor list with details and at least three filters
- [x] Head adds, edits and removes the department's professors
- [x] Head appoints a deputy, and must remove the current deputy first
- [x] Deputy adds, edits and removes the department's courses, with professors, TAs, number, prerequisites, corequisites, level, capacity, class and exam times

## Education services
- [x] Weekly plan and exams in time order, for students and professors
- [x] Recommendation request and its result, for undergraduate and master's students
- [x] Enrollment certificate for every student
- [x] Minor request with an average requirement, approved by both departments' deputies, with three states
- [x] Withdrawal approved by the deputy, which changes status and blocks login
- [x] Dormitory request for master's students, decided at random
- [x] Thesis defense slot for PhD students

## Grades
- [x] Students see temporary grades, object, and read the answer; final grades leave that list
- [x] Professors enter and edit a grade per student, within 0 to 20, rounded to 0.25
- [x] Temporary publication needs a grade for everyone, then final publication
- [x] Professors read objections, answer them and can change the grade
- [x] Deputy filters by class, student and professor, reads objections, and sees a course summary
- [x] Academic record: passed units, final grades, N/A for unfinished courses, weighted average
- [x] Deputy can search students and open their record

## Unit selection
- [x] Menu hidden except during the student's window, set by the deputy with filters
- [x] Department course list with at least three sort orders
- [x] Suggested courses and starred courses
- [x] Star, take and ask the deputy for each course; star, drop and change group for taken ones
- [x] Errors for full course, missing prerequisite, class clash, exam clash and more than one general course
- [x] Deputy accepts or rejects requests in the inbox
- [x] After the window, invalid courses are removed in a cascade and courseware pages appear

## Messages and messenger
- [x] Inbox of requests and system notices, with accept and reject
- [x] Chat list sorted by last message, chat room with name, photo, times and history
- [x] Text, picture, audio, video and PDF messages (files are attached and downloaded, not played inside the app)
- [x] Role-based contact lists, sending to one, several or all
- [x] Chat request by student or professor number
- [x] Administrator (user code 1) receives support messages, kept and sent later when offline
- [x] Registrar searches students as they type, sees full details and messages by filters

## Courseware
- [x] Course pages for enrolled students, professors and TAs, with calendar, content, exercises and exams in time order
- [x] Professor adds students or TAs by number, and the user gets a notice
- [x] Learning content with up to five text or media items; professor edits, deletes and adds; TA edits only
- [x] Exercises with open, close and free-submission times, description, PDF and answer type
- [x] Students see status, grade and times and submit an answer
- [x] Professor sees answers, downloads them and grades them
- [x] TA grades with names and numbers masked
- [x] Combined calendar of deadlines and final exams

## Network and offline
- [x] Client and server talk only through sockets, with an auth token per client
- [x] Many clients at once
- [x] Pages refresh by themselves, with no refresh button
- [x] Clients receive only what they need, never another user's password
- [x] Lost connection is announced, the app stays open, and a reconnect button appears
- [x] Offline banner, with home, weekly plan, exams, academic record, profile and the last ten messages per chat
- [x] Server refuses to run without its database and warns clients when it is lost

## Project quality
- [x] Sensible packages, naming and short methods, with logic separate from the interface
- [x] Configuration files for program constants and server text
- [-] Every interface label in a configuration file: the labels live beside their screens, which keeps them readable, and nothing needs translating
