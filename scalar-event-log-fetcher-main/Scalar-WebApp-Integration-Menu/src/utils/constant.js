const CLIENT_ID = "tfuo8d6oz56i1ljpdoeoboj0ccf04tak";
const CLIENT_SECRET = "a6xuRXxblm42vk5aeHewuYrzr5UZ80qv";

// const REDIRECT_URL = "https://test7.jeeni.in/scalar";
// const BASE_URL = "https://test7.jeeni.in";
// const SCALAR_BOX_URL = "https://test7.jeeni.in/scalar-box";



const REDIRECT_URL = "https://scalarbox.jeeni.in/scalar";
const BASE_URL = "https://scalarbox.jeeni.in";
const SCALAR_BOX_URL = "https://scalarbox.jeeni.in/scalar-box";


export { CLIENT_ID, CLIENT_SECRET, REDIRECT_URL, BASE_URL, SCALAR_BOX_URL };

export function isObjectPresent(set, id) {
  for (let obj of set) {
    if (obj.id === id) {
      return true;
    }
  }
  return false;
}

export function intersection1(setA, setB) {
  let intersectionSet = new Set();
  for (let item of setB) {
    if (isObjectPresent(setA, item.id)) {
      intersectionSet.add(item);
    }
  }
  return intersectionSet;
}

export function convertListToMap(list) {
  const allowedListMap = new Map();
  if (list === null) return allowedListMap;

  list.forEach((item) => {
    allowedListMap.set(item.id, item);
  });
  return allowedListMap;
}

export function convertMapToList(map) {
  const list = [];
  if (map === null) return list;
  map.forEach((value, key) => {
    list.push(value);
  });

  return list;
}

export function convertUtcToLocal(utcString) {
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
  return localDateTimeString;
}

export function convertUtcToLocalMap(utcString) {
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
  const map = new Map();
  map.set("DATE", localDateString);
  map.set("TIME", localTimeString);
  return map;
}
