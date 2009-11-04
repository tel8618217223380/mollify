package com.google.gwt.i18n.client.constants;

public class DateTimeConstants_fi implements com.google.gwt.i18n.client.constants.DateTimeConstants {
  public java.lang.String[] weekendRange() {
    String args[] = (String[]) cache.get("weekendRange");
    if (args == null) {
      String [] writer= {
        "7",
        "1",
      };
      cache.put("weekendRange", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] standaloneMonths() {
    String args[] = (String[]) cache.get("standaloneMonths");
    if (args == null) {
      String [] writer= {
        "tammikuuta",
        "helmikuuta",
        "maaliskuuta",
        "huhtikuuta",
        "toukokuuta",
        "kesäkuuta",
        "heinäkuuta",
        "elokuuta",
        "syyskuuta",
        "lokakuuta",
        "marraskuuta",
        "joulukuuta",
      };
      cache.put("standaloneMonths", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] weekdays() {
    String args[] = (String[]) cache.get("weekdays");
    if (args == null) {
      String [] writer= {
        "sunnuntaina",
        "maanantaina",
        "tiistaina",
        "keskiviikkona",
        "torstaina",
        "perjantaina",
        "lauantaina",
      };
      cache.put("weekdays", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] standaloneShortMonths() {
    String args[] = (String[]) cache.get("standaloneShortMonths");
    if (args == null) {
      String [] writer= {
        "tammi",
        "helmi",
        "maalis",
        "huhti",
        "touko",
        "kesä",
        "heinä",
        "elo",
        "syys",
        "loka",
        "marras",
        "joulu",
      };
      cache.put("standaloneShortMonths", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String firstDayOfTheWeek() {
    return "2";
  }
  
  public java.lang.String[] months() {
    String args[] = (String[]) cache.get("months");
    if (args == null) {
      String [] writer= {
        "tammikuuta",
        "helmikuuta",
        "maaliskuuta",
        "huhtikuuta",
        "toukokuuta",
        "kesäkuuta",
        "heinäkuuta",
        "elokuuta",
        "syyskuuta",
        "lokakuuta",
        "marraskuuta",
        "joulukuuta",
      };
      cache.put("months", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] eraNames() {
    String args[] = (String[]) cache.get("eraNames");
    if (args == null) {
      String [] writer= {
        "ennen Kristuksen syntymää",
        "jälkeen Kristuksen syntymän",
      };
      cache.put("eraNames", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] narrowMonths() {
    String args[] = (String[]) cache.get("narrowMonths");
    if (args == null) {
      String [] writer= {
        "T",
        "H",
        "M",
        "H",
        "T",
        "K",
        "H",
        "E",
        "S",
        "L",
        "M",
        "J",
      };
      cache.put("narrowMonths", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] shortMonths() {
    String args[] = (String[]) cache.get("shortMonths");
    if (args == null) {
      String [] writer= {
        "tammi",
        "helmi",
        "maalis",
        "huhti",
        "touko",
        "kesä",
        "heinä",
        "elo",
        "syys",
        "loka",
        "marras",
        "joulu",
      };
      cache.put("shortMonths", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] narrowWeekdays() {
    String args[] = (String[]) cache.get("narrowWeekdays");
    if (args == null) {
      String [] writer= {
        "S",
        "M",
        "T",
        "K",
        "T",
        "P",
        "L",
      };
      cache.put("narrowWeekdays", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] standaloneWeekdays() {
    String args[] = (String[]) cache.get("standaloneWeekdays");
    if (args == null) {
      String [] writer= {
        "sunnuntaina",
        "maanantaina",
        "tiistaina",
        "keskiviikkona",
        "torstaina",
        "perjantaina",
        "lauantaina",
      };
      cache.put("standaloneWeekdays", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] standaloneNarrowMonths() {
    String args[] = (String[]) cache.get("standaloneNarrowMonths");
    if (args == null) {
      String [] writer= {
        "T",
        "H",
        "M",
        "H",
        "T",
        "K",
        "H",
        "E",
        "S",
        "L",
        "M",
        "J",
      };
      cache.put("standaloneNarrowMonths", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] standaloneNarrowWeekdays() {
    String args[] = (String[]) cache.get("standaloneNarrowWeekdays");
    if (args == null) {
      String [] writer= {
        "S",
        "M",
        "T",
        "K",
        "T",
        "P",
        "L",
      };
      cache.put("standaloneNarrowWeekdays", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] ampms() {
    String args[] = (String[]) cache.get("ampms");
    if (args == null) {
      String [] writer= {
        "ap.",
        "ip.",
      };
      cache.put("ampms", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] dateFormats() {
    String args[] = (String[]) cache.get("dateFormats");
    if (args == null) {
      String [] writer= {
        "EEEE d. MMMM yyyy",
        "d. MMMM yyyy",
        "d.M.yyyy",
        "d.M.yyyy",
      };
      cache.put("dateFormats", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] shortWeekdays() {
    String args[] = (String[]) cache.get("shortWeekdays");
    if (args == null) {
      String [] writer= {
        "su",
        "ma",
        "ti",
        "ke",
        "to",
        "pe",
        "la",
      };
      cache.put("shortWeekdays", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] timeFormats() {
    String args[] = (String[]) cache.get("timeFormats");
    if (args == null) {
      String [] writer= {
        "H.mm.ss v",
        "H.mm.ss z",
        "H.mm.ss",
        "H.mm",
      };
      cache.put("timeFormats", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] standaloneShortWeekdays() {
    String args[] = (String[]) cache.get("standaloneShortWeekdays");
    if (args == null) {
      String [] writer= {
        "su",
        "ma",
        "ti",
        "ke",
        "to",
        "pe",
        "la",
      };
      cache.put("standaloneShortWeekdays", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] eras() {
    String args[] = (String[]) cache.get("eras");
    if (args == null) {
      String [] writer= {
        "eKr.",
        "jKr.",
      };
      cache.put("eras", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] quarters() {
    String args[] = (String[]) cache.get("quarters");
    if (args == null) {
      String [] writer= {
        "1. neljännes",
        "2. neljännes",
        "3. neljännes",
        "4. neljännes",
      };
      cache.put("quarters", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  public java.lang.String[] shortQuarters() {
    String args[] = (String[]) cache.get("shortQuarters");
    if (args == null) {
      String [] writer= {
        "1. nelj.",
        "2. nelj.",
        "3. nelj.",
        "4. nelj.",
      };
      cache.put("shortQuarters", writer);
      return writer;
    } else {
      return args;
    }
  }
  
  java.util.Map cache = new java.util.HashMap();
  }
