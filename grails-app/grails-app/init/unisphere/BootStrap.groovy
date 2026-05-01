package unisphere

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * Bootstrap - runs on application startup.
 * Seeds demo users, posts, comments, and notifications for presentation.
 */
class BootStrap {

    def init = { servletContext ->
        println "=== UniSphere BootStrap: Starting ==="

        try {
            User.withTransaction { status ->
                try {
                    int userCount = User.count()
                    println "=== BootStrap: Found ${userCount} existing users ==="

                    if (userCount == 0) {
                        seedData()
                    }
                } catch (Exception inner) {
                    println "=== BootStrap: Error: ${inner.message} ==="
                    inner.printStackTrace()
                    status.setRollbackOnly()
                }
            }
        } catch (Exception e) {
            println "=== BootStrap: Fatal error: ${e.message} ==="
            e.printStackTrace()
        }

        println "=== UniSphere BootStrap: Complete ==="
    }

    private void seedData() {
        def encoder = new BCryptPasswordEncoder()
        String pw = encoder.encode('admin123')
        String studentPw = encoder.encode('student123')

        // ===== USERS =====
        def admin = new User(username: 'Admin', email: 'admin@unisphere.edu', password: pw, role: 'admin', branch: 'Computer Science', semester: 4, bio: 'UniSphere Platform Administrator').save(flush: true, failOnError: true)
        def rahul = new User(username: 'Rahul Sharma', email: 'rahul@unisphere.edu', password: studentPw, role: 'student', branch: 'Computer Science', semester: 6, bio: 'Full-stack developer & open source enthusiast').save(flush: true, failOnError: true)
        def priya = new User(username: 'Priya Patel', email: 'priya@unisphere.edu', password: studentPw, role: 'student', branch: 'Electronics', semester: 4, bio: 'IoT researcher & robotics club lead').save(flush: true, failOnError: true)
        def arjun = new User(username: 'Arjun Mehta', email: 'arjun@unisphere.edu', password: studentPw, role: 'student', branch: 'Mechanical', semester: 5, bio: 'CAD design & 3D printing enthusiast').save(flush: true, failOnError: true)
        def neha = new User(username: 'Neha Gupta', email: 'neha@unisphere.edu', password: studentPw, role: 'student', branch: 'Information Technology', semester: 3, bio: 'UI/UX designer & hackathon winner').save(flush: true, failOnError: true)

        println "=== BootStrap: Created 5 users ==="

        // Helper for dates
        def cal = Calendar.getInstance()
        def today = new Date()
        cal.time = today; cal.add(Calendar.DAY_OF_MONTH, -1); def yesterday = cal.time
        cal.time = today; cal.add(Calendar.DAY_OF_MONTH, -2); def twoDaysAgo = cal.time
        cal.time = today; cal.add(Calendar.DAY_OF_MONTH, -3); def threeDaysAgo = cal.time
        cal.time = today; cal.add(Calendar.DAY_OF_MONTH, 5); def fiveDaysLater = cal.time
        cal.time = today; cal.add(Calendar.DAY_OF_MONTH, 10); def tenDaysLater = cal.time

        // Post Creator Helper to bypass ID binding issues
        def createPost = { String pId, String type, String pTitle, String desc, Date pDate, String auth, int pLikes ->
            def p = new Post(postType: type, title: pTitle, description: desc, date: pDate, author: auth, likes: pLikes)
            p.id = pId
            p.save(insert: true, flush: true, failOnError: true)
        }

        // ===== ANNOUNCEMENTS =====
        createPost('post_announce_001', 'announcements', 'End-Semester Examination Schedule Released', 'The examination schedule for the Spring 2026 semester has been published. All students must check their respective department notice boards and the university portal for detailed timetables. Exams begin on May 15th. Contact the exam cell for any queries.', today, 'admin@unisphere.edu', 24)
        createPost('post_announce_002', 'announcements', 'Campus Wi-Fi Upgrade — Maintenance Window', 'The IT department will be upgrading campus Wi-Fi infrastructure on May 3rd from 2:00 AM to 6:00 AM. Expect intermittent connectivity during this window. The upgrade will double bandwidth capacity across all buildings.', yesterday, 'admin@unisphere.edu', 15)
        createPost('post_announce_003', 'announcements', 'Library Hours Extended During Exam Period', 'The Central Library will remain open until midnight from May 5th through May 25th to support students during the examination period. Additional study rooms on the 3rd floor have been made available on a first-come, first-served basis.', twoDaysAgo, 'admin@unisphere.edu', 42)

        // ===== EVENTS =====
        createPost('post_event_001', 'events', 'TechFest 2026 — Annual Technical Symposium', 'Join us for TechFest 2026! Three days of hackathons, coding competitions, robotics challenges, and guest lectures by industry leaders from Google, Microsoft, and Amazon. Registration opens May 5th. Early bird discount available.', fiveDaysLater, 'admin@unisphere.edu', 67)
        createPost('post_event_002', 'events', 'Workshop: Introduction to Machine Learning with Python', 'A hands-on 3-hour workshop covering the fundamentals of ML using scikit-learn and TensorFlow. Bring your laptop with Python 3.10+ installed. Limited to 60 seats. Venue: Computer Lab 3, Block B.', tenDaysLater, 'admin@unisphere.edu', 38)
        createPost('post_event_003', 'events', 'Cultural Night — Rhythm & Blues', 'The annual cultural evening featuring live band performances, classical dance, stand-up comedy, and a DJ night. Food stalls from 6 PM onwards. Entry is free for all students with valid ID cards.', fiveDaysLater, 'admin@unisphere.edu', 89)

        // ===== LOST & FOUND =====
        createPost('post_lf_001', 'lostfound', 'Lost: Blue JBL Bluetooth Earbuds — Near Canteen', 'I lost my blue JBL earbuds with the charging case near the main canteen area yesterday around 4 PM. The case has a small scratch on the back. If found, please contact me. Reward offered!', yesterday, 'rahul@unisphere.edu', 5)
        createPost('post_lf_002', 'lostfound', 'Found: Student ID Card — Priya Verma, CSE 3rd Year', 'Found a student ID card near the parking lot of Block A. The card belongs to Priya Verma from CSE department. Please collect it from the security office at the main gate.', today, 'arjun@unisphere.edu', 8)
        createPost('post_lf_003', 'lostfound', 'Lost: HP Scientific Calculator — Exam Hall 2', 'Left my HP 35s scientific calculator in Exam Hall 2 after the Engineering Mathematics paper. It has my name written in marker on the back. Urgently needed for upcoming exams.', twoDaysAgo, 'neha@unisphere.edu', 3)

        // ===== RESOURCES =====
        createPost('post_res_001', 'resources', 'Data Structures & Algorithms — Complete Notes (Semester 3)', 'Sharing my compiled DSA notes covering Arrays, Linked Lists, Trees, Graphs, Dynamic Programming, and Sorting Algorithms. Includes solved examples from previous year papers and complexity analysis cheat sheets.', threeDaysAgo, 'rahul@unisphere.edu', 56)
        createPost('post_res_002', 'resources', 'Free Figma Templates for UI/UX Course Projects', 'Hey everyone! I\'ve created a collection of 15+ Figma templates specifically designed for our UI/UX course assignments. Includes mobile app wireframes, dashboard layouts, and design system components. Link in comments.', yesterday, 'neha@unisphere.edu', 34)
        createPost('post_res_003', 'resources', 'Circuit Design Lab Manual — Digital Electronics', 'Uploaded the complete lab manual for Digital Electronics with step-by-step experiment procedures, circuit diagrams, and expected outputs. Covers all 12 experiments including flip-flops, counters, and multiplexers.', twoDaysAgo, 'priya@unisphere.edu', 21)

        // ===== COMMUNITY (groups) =====
        createPost('post_grp_001', 'groups', 'Coding Club — Weekly LeetCode Challenge #12', 'This week\'s challenge: Solve 5 medium-level problems on Dynamic Programming. Top 3 performers get featured on our leaderboard! Join our Discord server for discussions and hints. Deadline: Sunday midnight.', today, 'rahul@unisphere.edu', 28)
        createPost('post_grp_002', 'groups', 'Photography Club — Campus Photo Walk This Saturday', 'Calling all photography enthusiasts! Join us for a campus photo walk this Saturday at 7 AM. Theme: "Golden Hour on Campus." Bring any camera — even your phone works! Best shots will be featured on the university Instagram page.', yesterday, 'priya@unisphere.edu', 19)
        createPost('post_grp_003', 'groups', 'Looking for Team Members — Smart India Hackathon 2026', 'We need 2 more members for our SIH team. Looking for someone with experience in React Native and Node.js. Our problem statement is in the Healthcare domain. DM if interested. Team meetings every Wednesday at 5 PM.', twoDaysAgo, 'neha@unisphere.edu', 31)

        // ===== COURSES =====
        createPost('post_course_001', 'courses', 'CS301 — Operating Systems: Mid-Sem Study Guide', 'Comprehensive study guide covering Process Management, CPU Scheduling (FCFS, SJF, Round Robin), Memory Management (Paging, Segmentation), and Deadlock concepts. Includes 50+ practice MCQs with solutions.', yesterday, 'rahul@unisphere.edu', 45)
        createPost('post_course_002', 'courses', 'EC205 — Signals & Systems: MATLAB Assignment Solutions', 'Sharing verified MATLAB code for all 8 assignments of Signals & Systems course. Includes Fourier Transform, Laplace Transform, and Z-Transform implementations with plots and explanations.', twoDaysAgo, 'priya@unisphere.edu', 27)
        createPost('post_course_003', 'courses', 'ME401 — Thermodynamics: Previous Year Question Papers (2020-2025)', 'Compiled 5 years of Thermodynamics question papers with marking schemes. Covers all units including Laws of Thermodynamics, Entropy, Carnot Cycle, and Rankine Cycle. Great for last-minute revision!', threeDaysAgo, 'arjun@unisphere.edu', 33)

        println "=== BootStrap: Created 18 posts ==="

        // ===== COMMENTS =====
        def commentData = [
            [postId: 'post_announce_001', email: 'rahul@unisphere.edu', content: 'Thanks for sharing! Can we get the detailed timetable as a PDF?', mins: 120],
            [postId: 'post_announce_001', email: 'priya@unisphere.edu', content: 'Will the supplementary exam dates be announced separately?', mins: 90],
            [postId: 'post_announce_003', email: 'neha@unisphere.edu', content: 'This is amazing! The 3rd floor study rooms are the best.', mins: 60],
            [postId: 'post_event_001', email: 'rahul@unisphere.edu', content: 'Can\'t wait for the hackathon! Already forming a team. 🚀', mins: 45],
            [postId: 'post_event_001', email: 'neha@unisphere.edu', content: 'Is there a registration fee for the workshops?', mins: 30],
            [postId: 'post_event_002', email: 'arjun@unisphere.edu', content: 'Do we need prior Python experience for this workshop?', mins: 200],
            [postId: 'post_res_001', email: 'neha@unisphere.edu', content: 'These notes are a lifesaver! Thanks Rahul! 📚', mins: 150],
            [postId: 'post_res_001', email: 'priya@unisphere.edu', content: 'The DP section is incredibly well-explained.', mins: 100],
            [postId: 'post_res_002', email: 'rahul@unisphere.edu', content: 'Neha your designs are always top-notch! 🎨', mins: 75],
            [postId: 'post_grp_001', email: 'arjun@unisphere.edu', content: 'Solved 3 out of 5 so far. The knapsack variant was tricky!', mins: 50],
            [postId: 'post_grp_003', email: 'rahul@unisphere.edu', content: 'I know React Native! DMing you now.', mins: 40],
            [postId: 'post_course_001', email: 'neha@unisphere.edu', content: 'The scheduling algorithms section helped me understand RR perfectly.', mins: 25],
            [postId: 'post_lf_001', email: 'priya@unisphere.edu', content: 'I think I saw someone turn in earbuds at the security office. Check there!', mins: 180],
        ]

        cal.time = today
        commentData.each { c ->
            cal.time = today
            cal.add(Calendar.MINUTE, -c.mins)
            new Comment(postId: c.postId, authorEmail: c.email, content: c.content, createdAt: cal.time).save(flush: true, failOnError: true)
        }

        println "=== BootStrap: Created ${commentData.size()} comments ==="

        // ===== NOTIFICATIONS =====
        def notifData = [
            [email: 'admin@unisphere.edu', msg: 'Rahul Sharma liked your post "End-Semester Examination Schedule Released"', type: 'like', read: false, mins: 30],
            [email: 'admin@unisphere.edu', msg: 'Priya Patel commented on "End-Semester Examination Schedule Released"', type: 'comment', read: false, mins: 60],
            [email: 'admin@unisphere.edu', msg: 'Your post "TechFest 2026" has reached 50 likes! 🎉', type: 'like', read: true, mins: 180],
            [email: 'rahul@unisphere.edu', msg: 'Neha Gupta liked your notes on "Data Structures & Algorithms"', type: 'like', read: false, mins: 45],
            [email: 'rahul@unisphere.edu', msg: 'Priya Patel commented: "The DP section is incredibly well-explained."', type: 'comment', read: false, mins: 90],
            [email: 'rahul@unisphere.edu', msg: 'Welcome to UniSphere! Complete your profile to get started.', type: 'info', read: true, mins: 1440],
            [email: 'neha@unisphere.edu', msg: 'Rahul Sharma liked your Figma templates post', type: 'like', read: false, mins: 20],
            [email: 'neha@unisphere.edu', msg: 'Rahul commented: "Neha your designs are always top-notch! 🎨"', type: 'comment', read: false, mins: 75],
            [email: 'priya@unisphere.edu', msg: 'Your circuit design lab manual got 20 likes!', type: 'like', read: true, mins: 200],
            [email: 'arjun@unisphere.edu', msg: 'Neha Gupta is looking for SIH team members — check it out!', type: 'mention', read: false, mins: 100],
        ]

        notifData.each { n ->
            cal.time = today
            cal.add(Calendar.MINUTE, -n.mins)
            new Notification(userEmail: n.email, message: n.msg, type: n.type, isRead: n.read, createdAt: cal.time).save(flush: true, failOnError: true)
        }

        println "=== BootStrap: Created ${notifData.size()} notifications ==="
        println "=== BootStrap: Default admin: admin@unisphere.edu / admin123 ==="
        println "=== BootStrap: Demo student: rahul@unisphere.edu / student123 ==="
    }

    def destroy = {
    }
}
