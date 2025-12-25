import { convertUtcToLocalMap } from "../utils/constants";

export default function DataAndTime({ dataAndTime, fontSize = "14px" }) {
  let date, time;

  if (dataAndTime !== undefined) {
    // console.log("dataAndTime:: ", dataAndTime);
    const map = convertUtcToLocalMap(dataAndTime);

    if (map !== null) {
      date = map.get("DATE");
      time = map.get("TIME");
    } else {
      console.log("Error converting UTC to local time.");
    }
  }

  return (
    <div style={{ display: "flex", alignItems: "baseline", gap: "8px" }}>
      <div style={{ fontSize: fontSize }}>{date}</div>
      <div style={{ fontSize: "11px" }}>{time}</div>
    </div>
  );
}
