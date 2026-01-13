export const CLIENT_ID = "tfuo8d6oz56i1ljpdoeoboj0ccf04tak";
export const CLIENT_SECRET = "a6xuRXxblm42vk5aeHewuYrzr5UZ80qv";

export const REDIRECT_URL = "https://scalarbox.jeeni.in/scalar-box";
export const BASE_URL = "https://scalarbox.jeeni.in";

//export const REDIRECT_URL = "https://test7.jeeni.in/scalar-box"; //"http://localhost:3000/scalar-box";

// export const REDIRECT_URL = "http://localhost:3000/scalar-box";

// export const BASE_URL = "https://test7.jeeni.in";

export const CARDS_KEYS = [
  "ALL",
  "UNDER_REVIEW",
  "NEWLY_ADDED",
  // "IS_FAVOURITE",
];

export const VERSION_HISTORY = "VERSION_HISTORY";
export const FILE_COPIES = "FILE_COPIES";
export const EVENT_HISTORY = "EVENT_HISTORY";

export function convertUtcToLocal(utcString) {
  if (utcString === undefined) {
    console.log("utcString is undefined. Aborting operation.");
    return ""; // or any other value indicating an error or absence of valid input
  }

  utcString = utcString.toString();
  // Parse the UTC string
  const year = utcString.slice(0, 4);
  const month = utcString.slice(4, 6);
  const day = utcString.slice(6, 8);
  const hour = utcString.slice(8, 10);
  const minute = utcString.slice(10, 12);
  const second = utcString.slice(12, 14);
  // Create a Date object with UTC values
  const utcDate = new Date(
    Date.UTC(year, month - 1, day, hour, minute, second)
  );

  const optionsDate = { year: "numeric", month: "2-digit", day: "2-digit" };
  const optionsTime = {
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
  };
  const localDateString = utcDate.toLocaleString("en-GB", optionsDate);
  const localTimeString = utcDate.toLocaleString("en-GB", optionsTime);

  // Combine date and time strings
  const localDateTimeString = `${localDateString} ${localTimeString}`;
  const map = new Map();
  map.set("DATE", localDateString);
  map.set("TIME", localTimeString);

  return localDateTimeString;
}

export function convertUtcToLocalMap(utcString) {
  if (utcString === undefined) {
    return ""; // or any other value indicating an error or absence of valid input
  }

  utcString = utcString.toString();
  // Parse the UTC string
  const year = utcString.slice(0, 4);
  const month = utcString.slice(4, 6);
  const day = utcString.slice(6, 8);
  const hour = utcString.slice(8, 10);
  const minute = utcString.slice(10, 12);
  const second = utcString.slice(12, 14);
  // Create a Date object with UTC values
  const utcDate = new Date(
    Date.UTC(year, month - 1, day, hour, minute, second)
  );

  const optionsDate = { year: "numeric", month: "2-digit", day: "2-digit" };
  const optionsTime = {
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
  };
  const localDateString = utcDate.toLocaleString("en-GB", optionsDate);
  const localTimeString = utcDate.toLocaleString("en-GB", optionsTime);

  // Combine date and time strings
  const localDateTimeString = `${localDateString} ${localTimeString}`;
  const map = new Map();
  map.set("DATE", localDateString);
  map.set("TIME", localTimeString);

  return map;
}
