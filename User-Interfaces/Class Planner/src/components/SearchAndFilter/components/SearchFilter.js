export default class SearchFilter {
  searchAndFilter(courses, tags, tagsAND, subject, minimumCredits, maximumCredits) {
    if (tags.length > 0) {
      let coursesAfterTagSearch = [];

      if (tagsAND) {
        for (const course of Object.values(courses)) {
          if (tags.every(val => course.keywords.includes(val))) {
            coursesAfterTagSearch.push(course);
          }
        }
      } else {
        for (const course of Object.values(courses)) {
          for (const keyword of course.keywords) {
            if (tags.includes(keyword)) {
              coursesAfterTagSearch.push(course);
              break;
            }
          }
        }
      }

      courses = coursesAfterTagSearch;
    }

    if (subject !== 'All') {
      let coursesAfterSubject = [];

      for (const course of Object.values(courses)) {
        if (course.subject === subject)
          coursesAfterSubject.push(course)
      }
      courses = coursesAfterSubject;
    }

    if (minimumCredits !== '') {
      let coursesAfterMinimumCredits = [];

      for( const course of Object.values(courses)) {
        if (course.credits >= parseInt(minimumCredits))
          coursesAfterMinimumCredits.push(course);
      }
      courses = coursesAfterMinimumCredits;
    }

    if (maximumCredits !== '') {
      let coursesAfterMaximumCredits = [];

      for (const course of Object.values(courses)) {
        if (course.credits <= parseInt(maximumCredits))
          coursesAfterMaximumCredits.push(course);
      }
      courses = coursesAfterMaximumCredits;
    }

    return courses;
  }
}