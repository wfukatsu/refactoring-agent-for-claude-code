import { convertUtcToLocalMap } from "../utils/constant";

export default function DataAndTime({ dataAndTime, fontSize = "15px" }) {
  if (dataAndTime === undefined) {
    return (
      <div style={{ display: "flex", alignItems: "baseline", gap: "8px" }}>
        <div style={{ fontSize: fontSize }}></div>
        <div style={{ fontSize: "11px" }}></div>
      </div>
    );
  }

  const map = convertUtcToLocalMap(dataAndTime);
  const date = map.get("DATE");
  const time = map.get("TIME");

  return (
    <div style={{ display: "flex", alignItems: "baseline", gap: "8px" }}>
      <div style={{ fontSize: fontSize }}>{date}</div>
      <div style={{ fontSize: "11px" }}>{time}</div>
    </div>
  );
}
