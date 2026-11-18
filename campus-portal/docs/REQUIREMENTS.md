# Requirements checklist

Taken from the two project briefs in `coursework/Project`. `[x]` means built and covered by a test or a run through the client. `[-]` means deliberately left out, with the reason.

## Accounts
- [ ] Login with username, password and a four digit picture captcha that can be refreshed and changes after a failure
- [ ] Passwords stored as hashes
- [ ] Password change forced after more than three hours away, with no way past it
- [ ] Demo data: every role at least twice, five departments with two courses each, students with courses
- [ ] Head of department exists only through the initial data

## Home and profile
- [ ] Home shows current time, exit, last login, photo, name and email, and reaches every page
- [ ] Students also see status, supervisor, registration permission and registration time
- [ ] Deputy can register a new student or professor with username and password
- [ ] Student and professor profiles with all listed fields and a default photo
- [ ] Email and phone can be changed
- [ ] Every user can change the theme

## Registration
- [ ] Course list with at least three filters
- [ ] Professor list with details and at least three filters
- [ ] Head adds, edits and removes the department's professors
- [ ] Head appoints a deputy, and must remove the current deputy first
- [ ] Deputy adds, edits and removes the department's courses, with professors, TAs, number, prerequisites, corequisites, level, capacity, class and exam times

## Education services
- [ ] Weekly plan and exams in time order, for students and professors
- [ ] Recommendation request and its result, for undergraduate and master's students
- [ ] Enrollment certificate for every student
- [ ] Minor request with an average requirement, approved by both departments' deputies, with three states
- [ ] Withdrawal approved by the deputy, which changes status and blocks login
- [ ] Dormitory request for master's students, decided at random
- [ ] Thesis defense slot for PhD students

## Grades
- [ ] Students see temporary grades, object, and read the answer; final grades leave that list
- [ ] Professors enter and edit a grade per student, within 0 to 20, rounded to 0.25
- [ ] Temporary publication needs a grade for everyone, then final publication
- [ ] Professors read objections, answer them and can change the grade
- [ ] Deputy filters by class, student and professor, reads objections, and sees a course summary
- [ ] Academic record: passed units, final grades, N/A for unfinished courses, weighted average
- [ ] Deputy can search students and open their record

## Unit selection
- [ ] Menu hidden except during the student's window, set by the deputy with filters
- [ ] Department course list with at least three sort orders
- [ ] Suggested courses and starred courses
- [ ] Star, take and ask the deputy for each course; star, drop and change group for taken ones
- [ ] Errors for full course, missing prerequisite, class clash, exam clash and more than one general course
- [ ] Deputy accepts or rejects requests in the inbox
- [ ] After the window, invalid courses are removed in a cascade and courseware pages appear

## Messages and messenger
- [ ] Inbox of requests and system notices, with accept and reject
- [ ] Chat list sorted by last message, chat room with name, photo, times and history
- [ ] Text, picture, audio, video and PDF messages
- [ ] Role-based contact lists, sending to one, several or all
- [ ] Chat request by student or professor number
- [ ] Administrator (user code 1) receives support messages, kept and sent later when offline
- [ ] Registrar searches students as they type, sees full details and messages by filters

## Courseware
- [ ] Course pages for enrolled students, professors and TAs, with calendar, content, exercises and exams in time order
- [ ] Professor adds students or TAs by number, and the user gets a notice
- [ ] Learning content with up to five text or media items; professor edits, deletes and adds; TA edits only
- [ ] Exercises with open, close and free-submission times, description, PDF and answer type
- [ ] Students see status, grade and times and submit an answer
- [ ] Professor sees answers, downloads them and grades them
- [ ] TA grades with names and numbers masked
- [ ] Combined calendar of deadlines and final exams

## Network and offline
- [ ] Client and server talk only through sockets, with an auth token per client
- [ ] Many clients at once
- [ ] Pages refresh by themselves, with no refresh button
- [ ] Clients receive only what they need, never another user's password
- [ ] Lost connection is announced, the app stays open, and a reconnect button appears
- [ ] Offline banner, with home, weekly plan, exams, academic record, profile and the last ten messages per chat
- [ ] Server refuses to run without its database and warns clients when it is lost

## Project quality
- [ ] Sensible packages, naming and short methods, with logic separate from the interface
- [ ] Configuration files for program constants and server text
- [-] Every interface label in a configuration file: the labels live beside their screens, which keeps them readable, and nothing needs translating
