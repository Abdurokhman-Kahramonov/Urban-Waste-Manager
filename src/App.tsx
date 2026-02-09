import { useEffect, useMemo, useState } from 'react'

const categories = [
  {
    id: 'illegal',
    name: 'Illegal dumping',
    detail: 'Large piles, bags, or construction waste left in public areas.',
  },
  {
    id: 'overflow',
    name: 'Overflowed container',
    detail: 'Bins full, trash spilling or scattered nearby.',
  },
  {
    id: 'missed',
    name: 'Missed pick-up',
    detail: 'Scheduled pick-up skipped for curbside bins.',
  },
]

const sampleReports = [
  {
    id: 'UW-1024',
    status: 'In progress',
    category: 'Illegal dumping',
    address: '12th Ave & Lakeside Dr',
    reportedAt: 'Today, 9:42 AM',
    timeline: ['Reported', 'Validated', 'Assigned', 'In progress'],
    priority: 'High',
  },
  {
    id: 'UW-1012',
    status: 'Cleaned',
    category: 'Overflowed container',
    address: 'Central Park Gate B',
    reportedAt: 'Yesterday, 4:18 PM',
    timeline: ['Reported', 'Validated', 'Assigned', 'Cleaned', 'Verified'],
    priority: 'Medium',
  },
]

const statusStyles: Record<string, string> = {
  Reported: 'bg-amber-100 text-amber-900 border-amber-200',
  'In progress': 'bg-sky-100 text-sky-900 border-sky-200',
  Cleaned: 'bg-emerald-100 text-emerald-900 border-emerald-200',
  Verified: 'bg-emerald-100 text-emerald-900 border-emerald-200',
}

const priorityStyles: Record<string, string> = {
  Low: 'bg-slate-100 text-slate-700',
  Medium: 'bg-amber-100 text-amber-700',
  High: 'bg-rose-100 text-rose-700',
}

const baseApiUrl =
  import.meta.env.VITE_BASE_API_URL ??
  import.meta.env.BASE_API_URL ??
  'https://example.com'

const AUTH_TOKEN_KEY = 'auth_token'

const routes = {
  home: '/',
  login: '/auth/login',
  register: '/auth/register',
}

function usePathname() {
  const [pathname, setPathname] = useState(() => window.location.pathname)

  useEffect(() => {
    const handlePopState = () => setPathname(window.location.pathname)
    window.addEventListener('popstate', handlePopState)
    return () => window.removeEventListener('popstate', handlePopState)
  }, [])

  const navigate = (path: string, replace = false) => {
    if (window.location.pathname === path) return
    if (replace) {
      window.history.replaceState({}, '', path)
    } else {
      window.history.pushState({}, '', path)
    }
    setPathname(path)
  }

  return { pathname, navigate }
}

function App() {
  const { pathname, navigate } = usePathname()
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [selectedCategory, setSelectedCategory] = useState(categories[0].id)

  const selectedCategoryDetail = useMemo(
    () => categories.find((item) => item.id === selectedCategory)?.detail ?? '',
    [selectedCategory],
  )

  const isAuthRoute = pathname.startsWith('/auth')
  const isKnownRoute = pathname === routes.home || isAuthRoute

  useEffect(() => {
    const token = localStorage.getItem(AUTH_TOKEN_KEY)
    setIsLoggedIn(Boolean(token))
  }, [])

  useEffect(() => {
    if (!isLoggedIn && pathname === routes.home) {
      navigate(routes.login, true)
      return
    }

    if (isLoggedIn && isAuthRoute) {
      navigate(routes.home, true)
      return
    }

    if (!isKnownRoute) {
      navigate(isLoggedIn ? routes.home : routes.login, true)
    }
  }, [isLoggedIn, isAuthRoute, isKnownRoute, navigate, pathname])

  const handleLoginSuccess = () => {
    localStorage.setItem(AUTH_TOKEN_KEY, 'demo-user-token')
    setIsLoggedIn(true)
    navigate(routes.home, true)
  }

  const handleLogout = () => {
    localStorage.removeItem(AUTH_TOKEN_KEY)
    setIsLoggedIn(false)
    navigate(routes.login, true)
  }

  return (
    <div className="relative min-h-screen bg-[#f6f4ef] text-slate-900">
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute -top-32 left-1/2 h-72 w-72 -translate-x-1/2 rounded-full bg-emerald-200/70 blur-3xl" />
        <div className="absolute right-0 top-40 h-80 w-80 translate-x-1/3 rounded-full bg-orange-200/70 blur-3xl" />
        <div className="absolute bottom-0 left-0 h-96 w-96 -translate-x-1/3 rounded-full bg-sky-200/70 blur-3xl" />
      </div>

      <header className="relative z-10 mx-auto flex w-full max-w-6xl items-center justify-between px-6 pb-12 pt-8">
        <div className="flex items-center gap-3">
          <div className="grid h-11 w-11 place-items-center rounded-2xl bg-emerald-600 text-white shadow-lg shadow-emerald-600/30">
            <span className="text-lg font-semibold">CW</span>
          </div>
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.2em] text-emerald-700">
              CleanCity
            </p>
            <p className="text-xs text-slate-600">User reporting portal</p>
          </div>
        </div>

        {isLoggedIn && (
          <nav className="hidden items-center gap-8 text-sm font-medium text-slate-700 md:flex">
            <button
              className="transition hover:text-slate-900"
              onClick={() => navigate(routes.home)}
            >
              Home
            </button>
            <a className="transition hover:text-slate-900" href="#report">
              Report
            </a>
            <a className="transition hover:text-slate-900" href="#track">
              Track
            </a>
            <a className="transition hover:text-slate-900" href="#faq">
              Help
            </a>
          </nav>
        )}

        <div className="flex items-center gap-3">
          {isLoggedIn ? (
            <button
              className="rounded-full border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:border-slate-400 hover:text-slate-900"
              onClick={handleLogout}
            >
              Log out
            </button>
          ) : (
            <>
              <button
                className="rounded-full border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:border-slate-400 hover:text-slate-900"
                onClick={() => navigate(routes.login)}
              >
                Log in
              </button>
              <button
                className="rounded-full bg-slate-900 px-4 py-2 text-sm font-semibold text-white shadow-lg shadow-slate-900/20 transition hover:-translate-y-0.5 hover:bg-slate-800"
                onClick={() => navigate(routes.register)}
              >
                Create account
              </button>
            </>
          )}
        </div>
      </header>

      <main className="relative z-10 mx-auto w-full max-w-6xl px-6 pb-20">
        {!isAuthRoute && isLoggedIn && (
          <>
            <section className="grid gap-10 pb-16 md:grid-cols-[1.1fr_0.9fr] md:items-center">
              <div className="space-y-6">
                <p className="inline-flex items-center gap-2 rounded-full bg-emerald-100 px-4 py-2 text-xs font-semibold uppercase tracking-[0.2em] text-emerald-700">
                  Community first
                  <span className="h-1.5 w-1.5 rounded-full bg-emerald-500" />
                  Fast responses
                </p>
                <h1 className="font-display text-4xl leading-tight text-slate-900 md:text-5xl">
                  Report trash in seconds and keep your neighborhood clean.
                </h1>
                <p className="text-lg text-slate-600">
                  Upload a photo, share your location, and track every report as it moves from
                  validation to cleanup. Built for residents who want real-time progress.
                </p>
                <div className="flex flex-wrap gap-4">
                  <button
                    className="rounded-full bg-emerald-600 px-6 py-3 text-sm font-semibold text-white shadow-lg shadow-emerald-600/30 transition hover:-translate-y-0.5 hover:bg-emerald-500"
                    onClick={() => navigate(routes.register)}
                  >
                    Start a report
                  </button>
                  <button
                    className="rounded-full border border-slate-300 px-6 py-3 text-sm font-semibold text-slate-700 transition hover:border-slate-400 hover:text-slate-900"
                    onClick={() => navigate(routes.login)}
                  >
                    I already have an account
                  </button>
                </div>
                <div className="grid gap-4 sm:grid-cols-3">
                  {[
                    { label: 'Avg response', value: '2.4 hrs' },
                    { label: 'Reports cleaned', value: '91%' },
                    { label: 'Active today', value: '148' },
                  ].map((stat) => (
                    <div
                      key={stat.label}
                      className="rounded-2xl border border-white/60 bg-white/70 p-4 shadow-sm backdrop-blur"
                    >
                      <p className="text-xs uppercase tracking-[0.2em] text-slate-500">
                        {stat.label}
                      </p>
                      <p className="text-2xl font-semibold text-slate-900">{stat.value}</p>
                    </div>
                  ))}
                </div>
              </div>
              <div className="rounded-3xl border border-white/70 bg-white/80 p-6 shadow-xl backdrop-blur">
                <div className="flex items-center justify-between">
                  <p className="text-sm font-semibold text-slate-700">Quick report</p>
                  <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold text-emerald-700">
                    60 sec flow
                  </span>
                </div>
                <div className="mt-6 space-y-4">
                  <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                    <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Step 1</p>
                    <p className="text-sm font-semibold text-slate-800">
                      Create an account or log in.
                    </p>
                  </div>
                  <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                    <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Step 2</p>
                    <p className="text-sm font-semibold text-slate-800">
                      Upload a photo and choose a category.
                    </p>
                  </div>
                  <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                    <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Step 3</p>
                    <p className="text-sm font-semibold text-slate-800">
                      Track validation and cleanup status.
                    </p>
                  </div>
                </div>
                <div className="mt-6 rounded-2xl bg-slate-900 px-5 py-4 text-white">
                  <p className="text-xs uppercase tracking-[0.2em] text-emerald-300">
                    Latest update
                  </p>
                  <p className="text-sm font-semibold">
                    Report UW-1024 is assigned. ETA 35 minutes.
                  </p>
                </div>
              </div>
            </section>

            <section id="report" className="grid gap-8 lg:grid-cols-[1.1fr_0.9fr]">
              <div className="rounded-3xl border border-white/70 bg-white/85 p-6 shadow-xl backdrop-blur">
                <div className="flex flex-wrap items-center justify-between gap-3">
                  <div>
                    <p className="text-sm font-semibold text-slate-700">New trash report</p>
                    <h2 className="font-display text-2xl text-slate-900">
                      Upload photos and location
                    </h2>
                  </div>
                  <span className="rounded-full bg-slate-900 px-3 py-1 text-xs font-semibold text-white">
                    Users only
                  </span>
                </div>

                <div className="mt-6 grid gap-5">
                  <label className="group flex cursor-pointer items-center justify-between rounded-2xl border border-dashed border-slate-300 bg-slate-50 p-6 transition hover:border-slate-400">
                    <div>
                      <p className="text-sm font-semibold text-slate-800">Upload before photo</p>
                      <p className="text-xs text-slate-500">JPG or PNG up to 10MB.</p>
                    </div>
                    <span className="rounded-full bg-slate-900 px-4 py-2 text-xs font-semibold text-white">
                      Browse
                    </span>
                    <input className="hidden" type="file" accept="image/*" />
                  </label>

                  <div className="grid gap-4 md:grid-cols-[1fr_auto]">
                    <div className="rounded-2xl border border-slate-200 bg-white px-4 py-3">
                      <p className="text-xs uppercase tracking-[0.2em] text-slate-500">
                        Location
                      </p>
                      <input
                        className="mt-2 w-full bg-transparent text-sm font-semibold text-slate-900 outline-none"
                        placeholder="Share location or type an address"
                      />
                    </div>
                    <button className="rounded-2xl bg-emerald-600 px-5 py-3 text-sm font-semibold text-white shadow-lg shadow-emerald-600/30 transition hover:-translate-y-0.5 hover:bg-emerald-500">
                      Use my location
                    </button>
                  </div>

                  <div>
                    <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Category</p>
                    <div className="mt-3 grid gap-3 md:grid-cols-3">
                      {categories.map((category) => (
                        <label
                          key={category.id}
                          className={`cursor-pointer rounded-2xl border p-4 transition ${
                            selectedCategory === category.id
                              ? 'border-emerald-400 bg-emerald-50'
                              : 'border-slate-200 bg-white hover:border-slate-300'
                          }`}
                        >
                          <input
                            className="sr-only"
                            type="radio"
                            name="category"
                            value={category.id}
                            checked={selectedCategory === category.id}
                            onChange={() => setSelectedCategory(category.id)}
                          />
                          <p className="text-sm font-semibold text-slate-900">{category.name}</p>
                          <p className="mt-1 text-xs text-slate-500">{category.detail}</p>
                        </label>
                      ))}
                    </div>
                    <p className="mt-2 text-xs text-slate-500">{selectedCategoryDetail}</p>
                  </div>

                  <div className="rounded-2xl border border-slate-200 bg-white px-4 py-3">
                    <p className="text-xs uppercase tracking-[0.2em] text-slate-500">Description</p>
                    <textarea
                      className="mt-2 min-h-[120px] w-full resize-none bg-transparent text-sm text-slate-800 outline-none"
                      placeholder="Add extra context, landmarks, or timing."
                    />
                  </div>

                  <div className="flex flex-wrap items-center justify-between gap-4">
                    <div>
                      <p className="text-sm font-semibold text-slate-800">Reporter details</p>
                      <p className="text-xs text-slate-500">
                        Required for login or updates.
                      </p>
                    </div>
                    <div className="flex flex-wrap gap-3">
                      <input
                        className="rounded-full border border-slate-200 bg-white px-4 py-2 text-sm outline-none"
                        placeholder="Phone or email"
                      />
                      <button className="rounded-full bg-slate-900 px-6 py-2 text-sm font-semibold text-white shadow-lg shadow-slate-900/20 transition hover:-translate-y-0.5 hover:bg-slate-800">
                        Submit report
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <div className="space-y-6">
                <div className="rounded-3xl border border-white/70 bg-white/85 p-6 shadow-xl backdrop-blur">
                  <p className="text-sm font-semibold text-slate-700">Live map</p>
                  <h3 className="font-display text-2xl text-slate-900">
                    Report location preview
                  </h3>
                  <div className="mt-4 overflow-hidden rounded-2xl border border-slate-200 bg-slate-100">
                    <div className="relative h-56 w-full bg-[radial-gradient(circle_at_top,_#c8f3e0,_transparent_55%),_radial-gradient(circle_at_bottom,_#fde0c6,_transparent_60%)]">
                      <div className="absolute left-6 top-6 rounded-2xl bg-white/90 px-3 py-2 text-xs font-semibold text-slate-700 shadow-sm">
                        Location pin preview
                      </div>
                      <div className="absolute right-6 top-16 h-4 w-4 rounded-full bg-emerald-600 shadow-lg shadow-emerald-600/40" />
                      <div className="absolute bottom-10 left-10 h-3 w-3 rounded-full bg-amber-500 shadow-lg shadow-amber-500/40" />
                    </div>
                  </div>
                  <div className="mt-4 flex items-center justify-between text-xs text-slate-500">
                    <span>Pin auto-updates when you share location.</span>
                    <span>Map placeholder</span>
                  </div>
                </div>

                <div className="rounded-3xl border border-white/70 bg-white/85 p-6 shadow-xl backdrop-blur">
                  <p className="text-sm font-semibold text-slate-700">Security</p>
                  <h3 className="font-display text-2xl text-slate-900">
                    Your report is protected
                  </h3>
                  <p className="mt-3 text-sm text-slate-600">
                    Photos and location are shared only with cleanup teams. Public views show
                    generalized areas, never exact addresses.
                  </p>
                  <div className="mt-4 flex flex-wrap gap-3 text-xs font-semibold text-slate-600">
                    {['Secure media storage', 'Private reporter info', 'Status notifications'].map(
                      (item) => (
                        <span
                          key={item}
                          className="rounded-full border border-slate-200 bg-slate-50 px-3 py-1"
                        >
                          {item}
                        </span>
                      ),
                    )}
                  </div>
                </div>
              </div>
            </section>

            <section id="track" className="mt-16 grid gap-8 lg:grid-cols-[1.1fr_0.9fr]">
              <div className="rounded-3xl border border-white/70 bg-white/85 p-6 shadow-xl backdrop-blur">
                <div className="flex flex-wrap items-center justify-between gap-3">
                  <div>
                    <p className="text-sm font-semibold text-slate-700">Track status</p>
                    <h2 className="font-display text-2xl text-slate-900">Your recent reports</h2>
                  </div>
                  <div className="flex items-center gap-2 rounded-full border border-slate-200 bg-white px-3 py-2 text-xs text-slate-600">
                    <span className="h-2 w-2 rounded-full bg-emerald-500" />
                    Live updates enabled
                  </div>
                </div>

                <div className="mt-6 space-y-4">
                  {sampleReports.map((report) => (
                    <div
                      key={report.id}
                      className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
                    >
                      <div className="flex flex-wrap items-center justify-between gap-3">
                        <div>
                          <p className="text-xs uppercase tracking-[0.2em] text-slate-500">
                            {report.id}
                          </p>
                          <p className="text-lg font-semibold text-slate-900">
                            {report.category}
                          </p>
                          <p className="text-xs text-slate-500">{report.address}</p>
                        </div>
                        <div className="flex items-center gap-2">
                          <span
                            className={`rounded-full border px-3 py-1 text-xs font-semibold ${
                              statusStyles[report.status] ?? statusStyles.Reported
                            }`}
                          >
                            {report.status}
                          </span>
                          <span
                            className={`rounded-full px-3 py-1 text-xs font-semibold ${
                              priorityStyles[report.priority]
                            }`}
                          >
                            {report.priority}
                          </span>
                        </div>
                      </div>

                      <div className="mt-4 flex flex-wrap items-center gap-3 text-xs text-slate-500">
                        <span>Reported: {report.reportedAt}</span>
                        <span>Next update in 20 minutes</span>
                      </div>

                      <div className="mt-4 flex flex-wrap gap-2">
                        {report.timeline.map((step, index) => (
                          <span
                            key={`${report.id}-${step}`}
                            className={`rounded-full px-3 py-1 text-xs font-semibold ${
                              index === report.timeline.length - 1
                                ? 'bg-slate-900 text-white'
                                : 'bg-slate-100 text-slate-600'
                            }`}
                          >
                            {step}
                          </span>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="space-y-6">
                <div className="rounded-3xl border border-white/70 bg-white/85 p-6 shadow-xl backdrop-blur">
                  <p className="text-sm font-semibold text-slate-700">Find a report</p>
                  <h3 className="font-display text-2xl text-slate-900">Track by report ID</h3>
                  <div className="mt-4 flex flex-wrap gap-3">
                    <input
                      className="flex-1 rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm outline-none"
                      placeholder="Enter report ID (e.g. UW-1024)"
                    />
                    <button className="rounded-2xl bg-slate-900 px-6 py-3 text-sm font-semibold text-white shadow-lg shadow-slate-900/20 transition hover:-translate-y-0.5 hover:bg-slate-800">
                      Track
                    </button>
                  </div>
                  <div className="mt-6 rounded-2xl border border-slate-200 bg-slate-50 p-4 text-sm text-slate-600">
                    Report UW-1024 is assigned to a driver. Estimated cleanup window: 11:10 AM -
                    12:20 PM.
                  </div>
                </div>

                <div className="rounded-3xl border border-white/70 bg-white/85 p-6 shadow-xl backdrop-blur">
                  <p className="text-sm font-semibold text-slate-700">Support</p>
                  <h3 className="font-display text-2xl text-slate-900">Need help?</h3>
                  <p className="mt-3 text-sm text-slate-600">
                    Use in-app chat or call the city hotline for urgent hazardous materials.
                  </p>
                  <div className="mt-4 grid gap-3 sm:grid-cols-2">
                    <button className="rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-semibold text-slate-700 transition hover:border-slate-300 hover:text-slate-900">
                      Chat support
                    </button>
                    <button className="rounded-2xl bg-emerald-600 px-4 py-3 text-sm font-semibold text-white shadow-lg shadow-emerald-600/30 transition hover:-translate-y-0.5 hover:bg-emerald-500">
                      Call hotline
                    </button>
                  </div>
                </div>
              </div>
            </section>

            <section id="faq" className="mt-16 rounded-3xl border border-white/70 bg-white/85 p-8 shadow-xl backdrop-blur">
              <div className="grid gap-8 md:grid-cols-[0.8fr_1.2fr]">
                <div>
                  <p className="text-sm font-semibold text-slate-700">How it works</p>
                  <h2 className="font-display text-3xl text-slate-900">
                    Simple reporting, real progress
                  </h2>
                  <p className="mt-3 text-sm text-slate-600">
                    Every report is validated, assigned to a driver, and verified after cleanup. You
                    see the status in real time, and notifications arrive the moment anything
                    changes.
                  </p>
                </div>
                <div className="grid gap-4 sm:grid-cols-2">
                  {[
                    {
                      title: 'Fast triage',
                      text: 'Reports are reviewed quickly to prevent duplicates or spam.',
                    },
                    {
                      title: 'Trusted evidence',
                      text: 'Before and after photos ensure the cleanup is verified.',
                    },
                    {
                      title: 'Live status',
                      text: 'Status chips update as soon as a driver accepts the task.',
                    },
                    {
                      title: 'Community impact',
                      text: 'See how many reports are cleared each week.',
                    },
                  ].map((item) => (
                    <div
                      key={item.title}
                      className="rounded-2xl border border-slate-200 bg-slate-50 p-4"
                    >
                      <p className="text-sm font-semibold text-slate-900">{item.title}</p>
                      <p className="mt-2 text-xs text-slate-500">{item.text}</p>
                    </div>
                  ))}
                </div>
              </div>
            </section>
          </>
        )}

        {pathname === routes.login && !isLoggedIn && (
          <section className="mx-auto max-w-3xl">
            <div className="rounded-3xl border border-white/70 bg-white/90 p-8 shadow-xl backdrop-blur">
              <p className="text-sm font-semibold text-slate-700">Welcome back</p>
              <h2 className="font-display text-3xl text-slate-900">Log in to your account</h2>
              <p className="mt-2 text-sm text-slate-600">
                Use your email or phone number to access reports and updates.
              </p>
              <form className="mt-6 grid gap-4">
                <input
                  className="rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm outline-none"
                  placeholder="Email or phone"
                />
                <input
                  className="rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm outline-none"
                  type="password"
                  placeholder="Password"
                />
                <div className="flex items-center justify-between text-xs text-slate-500">
                  <label className="flex items-center gap-2">
                    <input type="checkbox" className="h-4 w-4 rounded border-slate-300" />
                    Remember me
                  </label>
                  <span>Forgot password?</span>
                </div>
                <button
                  type="button"
                  className="rounded-2xl bg-slate-900 px-6 py-3 text-sm font-semibold text-white shadow-lg shadow-slate-900/20 transition hover:-translate-y-0.5 hover:bg-slate-800"
                  onClick={handleLoginSuccess}
                >
                  Log in
                </button>
              </form>
              <div className="mt-6 flex flex-wrap items-center justify-between gap-3 text-sm text-slate-600">
                <span>New here?</span>
                <button
                  className="font-semibold text-emerald-700"
                  onClick={() => navigate(routes.register)}
                >
                  Create an account
                </button>
              </div>
              <p className="mt-4 text-xs text-slate-500">
                API base: {baseApiUrl}
              </p>
            </div>
          </section>
        )}

        {pathname === routes.register && !isLoggedIn && (
          <section className="mx-auto max-w-3xl">
            <div className="rounded-3xl border border-white/70 bg-white/90 p-8 shadow-xl backdrop-blur">
              <p className="text-sm font-semibold text-slate-700">Get started</p>
              <h2 className="font-display text-3xl text-slate-900">Create your account</h2>
              <p className="mt-2 text-sm text-slate-600">
                Register once to report trash and track every update.
              </p>
              <form className="mt-6 grid gap-4">
                <div className="grid gap-4 md:grid-cols-2">
                  <input
                    className="rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm outline-none"
                    placeholder="First name"
                  />
                  <input
                    className="rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm outline-none"
                    placeholder="Last name"
                  />
                </div>
                <input
                  className="rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm outline-none"
                  placeholder="Email or phone"
                />
                <input
                  className="rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm outline-none"
                  type="password"
                  placeholder="Create password"
                />
                <button
                  type="button"
                  className="rounded-2xl bg-emerald-600 px-6 py-3 text-sm font-semibold text-white shadow-lg shadow-emerald-600/30 transition hover:-translate-y-0.5 hover:bg-emerald-500"
                  onClick={handleLoginSuccess}
                >
                  Create account
                </button>
              </form>
              <div className="mt-6 flex flex-wrap items-center justify-between gap-3 text-sm text-slate-600">
                <span>Already registered?</span>
                <button
                  className="font-semibold text-emerald-700"
                  onClick={() => navigate(routes.login)}
                >
                  Log in
                </button>
              </div>
              <p className="mt-4 text-xs text-slate-500">
                API base: {baseApiUrl}
              </p>
            </div>
          </section>
        )}
      </main>

      <footer className="relative z-10 border-t border-white/70 bg-white/80">
        <div className="mx-auto flex w-full max-w-6xl flex-wrap items-center justify-between gap-4 px-6 py-6 text-sm text-slate-600">
          <p>CleanCity user portal · Powered by community reporting</p>
          <div className="flex flex-wrap gap-4 text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">
            <span>Terms</span>
            <span>Privacy</span>
            <span>Accessibility</span>
          </div>
        </div>
      </footer>
    </div>
  )
}

export default App
