/* eslint-disable react/prop-types */
import { useSelector } from "react-redux";
import { BeatLoader } from "react-spinners";
import { useTranslation } from "react-i18next";

export default function AddButton({
  hasChange,
  addHandler,
  handlePopoverOpen,
  handlePopoverClose,
}) {
  const { isLoading } = useSelector((store) => store.folder);

  const {t,i18n} = useTranslation();

  return (
    <div className="flex justify-end p-2">
      {!isLoading ? (
        <button
          onClick={hasChange ? addHandler : null}
          aria-owns={open ? "mouse-over-popover" : undefined}
          onMouseEnter={handlePopoverOpen}
          onMouseLeave={handlePopoverClose}
          className={`bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full ${
            hasChange ? "" : "opacity-50 cursor-not-allowed"
          }`}
        >
          {t("rightClickAddButtonText")}
        </button>
      ) : (
        <div
          style={{
            display: "flex",
          }}
        >
          <BeatLoader
            className="bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full"
            color="#fff"
          />
        </div>
      )}
    </div>
  );
}
